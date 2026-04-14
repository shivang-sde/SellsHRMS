package com.sellspark.SellsHRMS.dto.verification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AadhaarOtpVerifyRequest {
    private String otp;
    private String refId; // reference ID from OTP generation step
}
