package com.sellspark.SellsHRMS.monitoring.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupListDTO {
    private String id;
    private String name;
    private String description;
    private Integer urlCount;
    private Integer memberCount;
    private LocalDateTime createdAt;
}
