// package com.sellspark.SellsHRMS.controller.api.payroll;

// import com.sellspark.SellsHRMS.dto.payroll.PayRunDTO;
// import com.sellspark.SellsHRMS.service.payroll.PayrollReportService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/payroll/reports")
// @RequiredArgsConstructor
// public class PayrollReportRestController {

//     private final PayrollReportService reportService;

//     @GetMapping("/salary-register")
//     public ResponseEntity<?> getSalaryRegister(@RequestParam Long orgId,
//                                                @RequestParam(required = false) Long payRunId) {
//         return ResponseEntity.ok(reportService.getSalaryRegister(orgId, payRunId));
//     }

//     @GetMapping("/statutory-summary")
//     public ResponseEntity<?> getStatutorySummary(@RequestParam Long orgId) {
//         return ResponseEntity.ok(reportService.getStatutorySummary(orgId));
//     }

//     @GetMapping("/tax-summary")
//     public ResponseEntity<?> getTaxSummary(@RequestParam Long orgId) {
//         return ResponseEntity.ok(reportService.getTaxSummary(orgId));
//     }

//     @GetMapping("/payrun-summary")
//     public ResponseEntity<?> getPayRunSummary(@RequestParam Long orgId) {
//         return ResponseEntity.ok(reportService.getPayRunSummaries(orgId));
//     }
// }
