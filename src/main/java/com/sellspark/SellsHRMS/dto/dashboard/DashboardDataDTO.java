package com.sellspark.SellsHRMS.dto.dashboard;

import java.util.List;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;
import com.sellspark.SellsHRMS.dto.event.EventResponseDTO;
import com.sellspark.SellsHRMS.dto.holiday.HolidayResponse;
import com.sellspark.SellsHRMS.dto.announcement.AnnouncementResponseDTO;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDataDTO {
    private List<EmployeeResponse> birthdays;
    private List<EmployeeResponse> anniversaries;
    private List<HolidayResponse> holidays;
    private List<EventResponseDTO> events;
    private List<AnnouncementResponseDTO> announcements;
}
