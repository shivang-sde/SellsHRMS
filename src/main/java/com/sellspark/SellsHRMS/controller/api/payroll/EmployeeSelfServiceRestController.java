// package com.sellspark.SellsHRMS.controller.api.payroll;

// import com.sellspark.SellsHRMS.service.payroll.EmployeeSelfService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/employee/self")
// @RequiredArgsConstructor
// public class EmployeeSelfServiceRestController {

//     private final EmployeeSelfService selfService;

//     @GetMapping("/slips")
//     public ResponseEntity<?> getMySlips(@RequestParam Long employeeId) {
//         return ResponseEntity.ok(selfService.getPayslips(employeeId));
//     }

//     @GetMapping("/tax")
//     public ResponseEntity<?> getTaxSummary(@RequestParam Long employeeId) {
//         return ResponseEntity.ok(selfService.getTaxSummary(employeeId));
//     }

//     @GetMapping("/statutory")
//     public ResponseEntity<?> getStatutoryDetails(@RequestParam Long employeeId) {
//         return ResponseEntity.ok(selfService.getStatutoryDetails(employeeId));
//     }

//     @PostMapping("/investments")
//     public ResponseEntity<?> submitInvestments(@RequestParam Long employeeId,
//                                                @RequestBody Object investments) {
//         return ResponseEntity.ok(selfService.submitInvestments(employeeId, investments));
//     }
// }
