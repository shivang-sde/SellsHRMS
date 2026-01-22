package com.sellspark.SellsHRMS.dto.holiday;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HolidayRequest {
    private Long orgId;
    private LocalDate holidayDate;
    private String holidayName;
    private String holidayType; // PUBLIC, COMPANY_SPECIFIC, OPTIONAL
    private Boolean isMandatory;
    private String description;
}