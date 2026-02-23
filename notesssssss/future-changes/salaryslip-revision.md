Goal

When a salary slip has a mistake or needs correction:
The accountant can update certain fields (e.g., lopDays, or HRA component amount).
Each change is tracked in tbl_slip_component_change with:
Who changed it
When
From what → To what
Why (reason)
IP, browser, etc.
The system regenerates salary slip calculations and the PDF, and marks it as a revised version.

@Transactional
public SalarySlip reviseSlip(Long slipId, Map<String, Object> updates, String reason, String ipAddress, User user) {
    SalarySlip slip = slipRepo.findById(slipId)
            .orElseThrow(() -> new HRMSException("Salary slip not found", "SLIP_NOT_FOUND", HttpStatus.NOT_FOUND));

    // Snapshot before changes
    String beforeJson = objectMapper.writeValueAsString(slip);

    // Apply manual changes
    if (updates.containsKey("lopDays")) {
        slip.setLopDays((Double) updates.get("lopDays"));
    }

    if (updates.containsKey("paymentDays")) {
        slip.setPaymentDays((Double) updates.get("paymentDays"));
    }

    // Handle component changes
    if (updates.containsKey("components")) {
        List<Map<String, Object>> newComponents = (List<Map<String, Object>>) updates.get("components");
        updateSlipComponents(slip, newComponents);
    }

    // Recalculate totals
    recalculateSlipAmounts(slip);

    // Increment revision
    slip.setRevisionNo(slip.getRevisionNo() + 1);
    slip.setLastModifiedIp(ipAddress);
    slip.setLastModifiedBy(user.getId());
    slip.setStatus(SalarySlip.SlipStatus.GENERATED);
    slipRepo.save(slip);

    // Store change log
    SalarySlipComponentChange change = SalarySlipComponentChange.builder()
            .salarySlip(slip)
            .changedBy(user)
            .changedAt(LocalDateTime.now())
            .fieldName("MULTIPLE_FIELDS")
            .beforeJson(beforeJson)
            .afterJson(objectMapper.writeValueAsString(slip))
            .reason(reason)
            .ipAddress(ipAddress)
            .build();

    changeRepo.save(change);

    // Regenerate PDF
    payslipPdfService.generatePayslipPDF(salarySlipMapper.toDTO(slip));

    return slip;
}

Frontend (Accountant UI)
Provide an interface on the salary slip detail page:

| Field    | Current | Editable | Notes                 |
| -------- | ------- | -------- | --------------------- |
| LOP Days | 2       | ✅        | Adjust manually       |
| HRA      | ₹4,000  | ✅        | Adjust manually       |
| Bonus    | ₹2,000  | ✅        | Add/remove components |

When “Save & Regenerate” is clicked:
Call PATCH /api/accountant/{orgId}/slip/{slipId}/revise
Body example:
{
  "lopDays": 1,
  "components": [
    { "componentName": "HRA", "amount": 4500.0 }
  ],
  "reason": "Corrected LOP & housing allowance miscalculation"
}



@PostMapping("/{orgId}/slip/{slipId}/revise")
public ResponseEntity<?> reviseSlip(
        @PathVariable Long orgId,
        @PathVariable Long slipId,
        @RequestBody SalarySlipRevisionRequest req,
        HttpServletRequest servletRequest,
        Authentication auth) {

    User currentUser = userService.getCurrentUser(auth);
    String ip = servletRequest.getRemoteAddr();

    SalarySlip updatedSlip = accountantService.reviseSlip(
            slipId, req.getUpdates(), req.getReason(), ip, currentUser);

    return ResponseEntity.ok(Map.of(
            "message", "Salary slip revised successfully",
            "revision", updatedSlip.getRevisionNo(),
            "status", updatedSlip.getStatus().name()
    ));
}



Regenerating the PDF
Once the slip is revised
Run the same logic that generateSlipPdf() uses.
Update the pdfUrl with a version suffix:
String fileName = "payslip-" + empCode + "-v" + slip.getRevisionNo() + ".pdf";
slip.setPdfUrl(baseUrl + uploadUrlPath + "/" + fileName);

So previous versions stay preserved on disk or S3 for audit.

Viewing Change History (for Auditors/Admin)
Add a tab in the accountant dashboard to list changes:
| Date       | Changed By                                                | Field     | Before → After | Reason         | IP           |
| ---------- | --------------------------------------------------------- | --------- | -------------- | -------------- | ------------ |
| 2026-02-17 | [accountant@sellspark.in](mailto:accountant@sellspark.in) | `lopDays` | 2 → 1          | LOP adjustment | 192.168.1.12 |



Database Snapshot Summary

| Table                                  | Purpose                                                                         |
| -------------------------------------- | ------------------------------------------------------------------------------- |
| `tbl_salary_slip`                      | Stores latest version of the payslip                                            |
| `tbl_salary_slip_component`            | Components (earnings/deductions) for that slip                                  |
| `tbl_slip_component_change`            | Full audit log of manual changes                                                |
| `tbl_salary_slip_history` *(optional)* | Stores frozen JSON snapshot per revision, if you want full time-travel recovery |

End-to-End Behavior
| Action                                     | Result                                                     |
| ------------------------------------------ | ---------------------------------------------------------- |
| Accountant detects incorrect LOP or amount | Opens slip and edits                                       |
| Accountant submits correction              | System stores change, recalculates totals, regenerates PDF |
| Old PDF and revision retained              | New version has `v2`, `v3`, etc.                           |
| Audit dashboard                            | Shows what changed, who changed, from which IP, and why    |

