package com.sellspark.SellsHRMS.dto.verification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstVerificationRequest {
    private String gstin; // 15-character GSTIN
}
