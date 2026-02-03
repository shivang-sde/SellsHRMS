package com.sellspark.SellsHRMS.dto.dashboard;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardProjectStats {
    private long totalProjects;
    private long activeProjects;
    private long completedProjects;

    private long activeTickets;
    private long completedTickets;
    private long overdueTickets;

    private long pendingTasks;
    private long completedTasks;
    private long overdueTasks;

    private double overallCompletionRate; // percentage
}
