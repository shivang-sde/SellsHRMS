package com.sellspark.SellsHRMS.dto.verification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanVerificationRequest {
    private String pan;
    private String name;       // name to match against PAN holder
    private String dateOfBirth; // optional, format: DD/MM/YYYY
}
