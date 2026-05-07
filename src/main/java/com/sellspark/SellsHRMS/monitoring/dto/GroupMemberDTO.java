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
public class GroupMemberDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private LocalDateTime addedAt;
    private String addedByName;
}
