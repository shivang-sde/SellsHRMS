package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;

@Data
public class EmployeeDocumentRequest {
    private Long employeeId;
    private String documentType;
    private String externalUrl;
    private boolean removeFile; // optional: remove existing server-stored file
}
