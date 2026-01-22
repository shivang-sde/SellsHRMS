package com.sellspark.SellsHRMS.entity;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String line1;
    private String line2;

    private String city;
    private String state;
    private String country;
    private String pincode;
}

