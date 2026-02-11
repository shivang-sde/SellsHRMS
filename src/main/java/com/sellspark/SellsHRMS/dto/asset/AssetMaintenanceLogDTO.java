package com.sellspark.SellsHRMS.dto.asset;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenanceLogDTO {
    private Long id;

    private Long assetId;
    private String assetCode;
    private String assetName;

    private LocalDate maintenanceDate;
    private String description;
    private Double cost;
    private String performedBy;

    private Long orgId;
}
