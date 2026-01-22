package com.sellspark.SellsHRMS.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBankResponse {
    private Long id;

     private Long employeeId;
    private String employeeName;

    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    private Boolean isPrimaryAccount;
}
