package com.sellspark.SellsHRMS.dto.dashboard;


import java.util.List;
import lombok.*;




@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MyWorkSummaryDTO {

    private Long employeeId;
    private String employeeName;

    private List<ProjectSummaryDTO> projects;
    private List<TicketSummaryDTO> tickets;
    private List<TaskSummaryDTO> tasks;

    private DashboardProjectStats stats;
}
