package com.sellspark.SellsHRMS.dto.asset;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private Long id;
    private String assetCode;
    private String name;
    private String description;
    private LocalDate purchaseDate;
    private Double cost;
    private String condition; // enum name
    private String status; // enum name

    private Long categoryId;
    private String categoryName;

    private Long vendorId;
    private String vendorName;

    private Long assignedToId;
    private String assignedToName;

    private Long orgId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
