package com.sellspark.SellsHRMS.service;


import com.sellspark.SellsHRMS.dto.employee.EmployeeBankRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeDocumentService {
    List<EmployeeDocumentResponse> getByEmployee(Long employeeId);

    EmployeeDocumentResponse uploadFile(Long employeeId, String documentType, MultipartFile file) throws Exception;

    EmployeeDocumentResponse saveLink(EmployeeDocumentRequest req);

    // List<EmployeeDocumentResponse> getDocuments(Long employeeId);

    void delete(Long documentId) throws Exception;
}
