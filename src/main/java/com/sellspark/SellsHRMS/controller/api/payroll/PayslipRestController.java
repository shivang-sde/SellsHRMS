package com.sellspark.SellsHRMS.controller.api.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.payroll.SalarySlipRepository;
import com.sellspark.SellsHRMS.service.FileStorageService;
import com.sellspark.SellsHRMS.service.impl.payroll.SalarySlipServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll/salary-slips")
@RequiredArgsConstructor
public class PayslipRestController {

    @Value("${app.upload.base-dir}")
    private String baseDir;

    @Value("{${app.upload.base-dir}")
    private String uploadBaseDir;

    private final SalarySlipServiceImpl slipService;
    private final SalarySlipRepository slipRepository;
    private final FileStorageService fileStorageService;

    // ✅ Get Salary Slip DTO (for UI or API display)
    @GetMapping("/{id}")
    public ResponseEntity<SalarySlipDTO> getSlip(@PathVariable Long id) {
        return ResponseEntity.ok(slipService.getSlipDtoById(id));
    }

    @PostMapping("/{id}/generate-pdf")
public ResponseEntity<?> generatePdf(@PathVariable Long id, @RequestParam Long orgId) {
    try {
        SalarySlip slip = slipService.generatePdfForSlip(id, orgId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payslip PDF generated successfully",
                "pdfUrl", slip.getPdfUrl()
        ));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", e.getMessage()
        ));
    }
}




    // ✅ Download payslip PDF stored in file system
    // ✅ Download payslip PDF stored in file system
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPayslipPdf(@PathVariable Long id) throws Exception {
    SalarySlip slip = slipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payslip not found"));

    if (slip.getPdfPath() == null || slip.getPdfPath().isBlank()) {
        throw new ResourceNotFoundException("Payslip PDF not generated yet for ID " + id);
    }

    // Resolve filesystem path
    Path filePath = Paths.get(uploadBaseDir).resolve(slip.getPdfPath()).normalize();
    Resource resource = new UrlResource(filePath.toUri());

    if (!resource.exists()) {
        throw new ResourceNotFoundException("Payslip file missing from storage");
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
}



    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getAllForEmployee(@PathVariable Long empId) {
        try {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", slipService.getAllByEmployee(empId)
            ));
        } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to fetch salary slips"
        ));
    }
}

}
