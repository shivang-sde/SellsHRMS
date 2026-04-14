package com.sellspark.SellsHRMS.dto.verification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AadhaarOtpRequest {
    private String aadhaarNumber; // 12-digit Aadhaar number
}
