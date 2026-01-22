package com.sellspark.SellsHRMS.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentResponse;
import com.sellspark.SellsHRMS.service.EmployeeDocumentService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/employee/documents")
@RequiredArgsConstructor
public class EmployeeDoccumentRestController {
    
    private final EmployeeDocumentService docService;

    @GetMapping("/{empId}")
    public ResponseEntity<List<EmployeeDocumentResponse>> list(@PathVariable Long empId) {
        return ResponseEntity.ok(docService.getByEmployee(empId));
    }
    

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<EmployeeDocumentResponse>upload(
        @RequestParam("employeeId") Long empId,
        @RequestParam("documentType") String docType,
        @RequestParam("file") MultipartFile file) throws Exception {
            return ResponseEntity.ok(docService.uploadFile(empId, docType, file));
    }

    @PostMapping("/link")
    public ResponseEntity<EmployeeDocumentResponse> saveLink(@RequestBody EmployeeDocumentRequest req) {
        return ResponseEntity.ok(docService.saveLink(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        docService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    
}
