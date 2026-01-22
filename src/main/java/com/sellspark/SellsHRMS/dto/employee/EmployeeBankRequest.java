package com.sellspark.SellsHRMS.dto.employee;

import lombok.Data;

@Data
public class EmployeeBankRequest {
    private Long employeeId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private Boolean isPrimaryAccount;
}
