package com.sellspark.SellsHRMS.dto.organisation;

import java.time.LocalDate;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
   private Long id; private String title; private String description; private LocalDate startDate; private LocalDate endDate; private String location; private String type; // MEETING, CELEBRATION, etc
}
