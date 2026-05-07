package com.sellspark.SellsHRMS.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDetailDTO {
    private MonitorUrlDTO url;
    private List<CheckDTO> checks;
    private List<IncidentDTO> incidents;
    private List<GroupBasicDTO> groups;
}
