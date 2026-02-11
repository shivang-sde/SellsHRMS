package com.sellspark.SellsHRMS.dto.asset;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetAssignmentDTO {
    private Long id;

    private Long assetId;
    private String assetCode;
    private String assetName;

    private Long employeeId;
    private String employeeName;

    private LocalDate assignedDate;
    private LocalDate returnDate;
    private String remarks;
    private Boolean activeFlag;

    private Long orgId;
}
