package com.sellspark.SellsHRMS.dto.users;

import lombok.*;

@Data
@Getter
@Setter
public class ChanagePassworrdRequest {

    private String email;
    private String currentPassword;
    private String newPassword;
}
