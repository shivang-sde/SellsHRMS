package com.sellspark.SellsHRMS.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private Long id;
    private String module;
    private String action;
    private String code;
}
