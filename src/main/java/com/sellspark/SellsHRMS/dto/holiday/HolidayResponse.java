package com.sellspark.SellsHRMS.dto.holiday;

import lombok.Data;
import java.time.LocalDate;
 
@Data
public class HolidayResponse {
    private Long id;
    private Long orgId;
    private LocalDate holidayDate;
    private String holidayName;
    private String holidayType;
    private Boolean isMandatory;
    private String description;
}