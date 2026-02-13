package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.dto.common.PagedResponse;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.dto.users.AccountantCreateRequest;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.service.payroll.AccountantService;
import com.sellspark.SellsHRMS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/accountant")
@RequiredArgsConstructor
public class AccountantRestController {

    private final AccountantService accountantService;
    private final UserService userService;

    @PostMapping("/{orgId}/create")
    public ResponseEntity<ApiResponse<User>> createAccountant(
            @PathVariable Long orgId,
            @RequestBody AccountantCreateRequest request) {
        try {
            User accountant = userService.createAccountantUser(
                    request.getEmail(),
                    request.getPassword(),
                    orgId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Accountant created successfully", accountant));
        } catch (Exception ex) {
            log.error("Error creating accountant: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/{orgId}/slips")
    public ResponseEntity<ApiResponse<PagedResponse<SalarySlipDTO>>> getSlips(
            @PathVariable Long orgId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean credited,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creditedAt,desc") String sort) {
        log.info("Fetching salary slips for organization: {}", orgId);
        log.info("Month: {}", month);
        log.info("Year: {}", year);
        log.info("Credited: {}", credited);
        log.info("Department ID: {}", departmentId);
        log.info("Search: {}", search);
        log.info("Page: {}", page);
        log.info("Size: {}", size);
        log.info("Sort: {}", sort);

        PagedResponse<SalarySlipDTO> result = accountantService.getSalarySlips(
                orgId, month, year, credited, departmentId, search, page, size, sort);

        return ResponseEntity.ok(ApiResponse.ok("Salary slips fetched successfully", result));
    }

    @PostMapping("/{orgId}/mark-credited/{slipId}")
    public ResponseEntity<ApiResponse<SalarySlipDTO>> markSalaryCredited(
            @PathVariable Long orgId,
            @PathVariable Long slipId,
            @RequestParam Long accountantUserId) {

        SalarySlipDTO result = accountantService.markSalaryCredited(orgId, slipId, accountantUserId);
        return ResponseEntity.ok(ApiResponse.ok("Salary slip marked as credited", result));
    }

    @PostMapping("/{orgId}/mark-credited/bulk")
    public ResponseEntity<ApiResponse<PagedResponse<SalarySlipDTO>>> markBulkSalaryCredited(
            @PathVariable Long orgId,
            @RequestParam Long accountantUserId,
            @RequestBody List<Long> slipIds) {

        PagedResponse<SalarySlipDTO> result = accountantService.markBulkSalaryCredited(orgId, slipIds,
                accountantUserId);
        return ResponseEntity.ok(ApiResponse.ok("Salary slips credited successfully", result));
    }

    @PostMapping("/{orgId}/slip/{slipId}/generate-pdf")
    public ResponseEntity<?> generatePdfForSlip(@PathVariable Long orgId, @PathVariable Long slipId) {
        try {
            String pdfUrl = accountantService.generateSlipPdf(orgId, slipId);
            return ResponseEntity.ok(ApiResponse.ok("Payslip generated successfully", pdfUrl));
        } catch (Exception ex) {
            log.error("❌ Error generating payslip PDF: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/{orgId}/slips/generate-pdf/bulk")
    public ResponseEntity<?> generatePdfBulk(
            @PathVariable Long orgId,
            @RequestBody List<Long> slipIds) {
        try {
            List<String> pdfUrls = accountantService.generateBulkSlipPdfs(orgId, slipIds);
            return ResponseEntity.ok(Map.of(
                    "message", "Payslips generated successfully",
                    "count", pdfUrls.size()));
        } catch (Exception ex) {
            log.error("❌ Error generating bulk payslips: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

}
