package com.sellspark.SellsHRMS.dto.asset;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Long orgId;
}
