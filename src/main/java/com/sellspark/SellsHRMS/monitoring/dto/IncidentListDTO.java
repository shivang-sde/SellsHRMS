package com.sellspark.SellsHRMS.monitoring.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentListDTO {
    private List<IncidentDTO> incidents;
    private PaginationDTO pagination;
}