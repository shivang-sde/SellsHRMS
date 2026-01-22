package com.sellspark.SellsHRMS.dto.dashboard;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardProjectStats {
    private long totalProjects;
    private long activeTickets;
    private long completedTickets;
    private long pendingTasks;
    private long completedTasks;
}
