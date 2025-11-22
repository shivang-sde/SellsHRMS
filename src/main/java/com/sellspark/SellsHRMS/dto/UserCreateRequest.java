package com.sellspark.SellsHRMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserCreateRequest {
    private Long employeeId;
    private String email;
    private String password;
    private Long roleId;
}
