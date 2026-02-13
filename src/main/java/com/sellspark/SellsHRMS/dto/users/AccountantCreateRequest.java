package com.sellspark.SellsHRMS.dto.users;

import lombok.Data;

@Data
public class AccountantCreateRequest {
    private String email;
    private String password;
}
