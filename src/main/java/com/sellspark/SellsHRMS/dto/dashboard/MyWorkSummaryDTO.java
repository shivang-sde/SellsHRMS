package com.sellspark.SellsHRMS.dto.dashboard;

import java.util.List;

import com.sellspark.SellsHRMS.dto.project.ReminderDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyWorkSummaryDTO {

    private Long employeeId;
    private String employeeName;

    // Core sections
    private List<ProjectSummaryDTO> activeProjects;
    private List<TaskSummaryDTO> assignedTasks;
    private List<TicketSummaryDTO> assignedTickets;

    // Upcoming work
    private List<ReminderDTO> upcomingDeadlines;

    // Aggregated stats
    private DashboardProjectStats stats;
}
