package com.sellspark.SellsHRMS.dto.organisation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;            // Department ID
    private String name;        // Department name
    private String description; // Department description
    private Long orgId;         // Organisation ID
    private String orgName;     // Organisation name (optional, for display)
}
