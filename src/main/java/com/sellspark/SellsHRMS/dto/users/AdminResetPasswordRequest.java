package com.sellspark.SellsHRMS.dto.users;

import lombok.Data;

@Data
public class AdminResetPasswordRequest {
    private Long userId;
    private String newPassword;
}
