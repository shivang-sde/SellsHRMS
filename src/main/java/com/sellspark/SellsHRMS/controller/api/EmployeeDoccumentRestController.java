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
@org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_VIEW_ALL', 'EMPLOYEE_CREATE', 'EMPLOYEE_EDIT')")
public class EmployeeDoccumentRestController {
    
    private final EmployeeDocumentService docService;

    @GetMapping("/{empId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_VIEW_SELF', 'EMPLOYEE_VIEW_TEAM', 'EMPLOYEE_VIEW_ALL')")
    public ResponseEntity<List<EmployeeDocumentResponse>> list(@PathVariable Long empId) {
        return ResponseEntity.ok(docService.getByEmployee(empId));
    }
    

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE', 'EMPLOYEE_EDIT')")
    public ResponseEntity<EmployeeDocumentResponse>upload(
        @RequestParam("employeeId") Long empId,
        @RequestParam("documentType") String docType,
        @RequestParam("file") MultipartFile file) throws Exception {
            return ResponseEntity.ok(docService.uploadFile(empId, docType, file));
    }

    @PostMapping("/link")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_CREATE', 'EMPLOYEE_EDIT')")
    public ResponseEntity<EmployeeDocumentResponse> saveLink(@RequestBody EmployeeDocumentRequest req) {
        return ResponseEntity.ok(docService.saveLink(req));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'EMPLOYEE_EDIT')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        docService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    
}
