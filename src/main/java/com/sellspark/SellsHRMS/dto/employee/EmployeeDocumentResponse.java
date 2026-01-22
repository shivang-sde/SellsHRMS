package com.sellspark.SellsHRMS.dto.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeDocumentResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String documentType;
    private String fileUrl;
    private String externalUrl;
    private String originalFilename;
    private String contentType;
    private LocalDateTime uploadedAt;
    private Boolean verified;
}
