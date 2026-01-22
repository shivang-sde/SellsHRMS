package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.dashboard.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.DashboardProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardProjectServiceImpl implements DashboardProjectService {

    private final EmployeeRepository empRepo;
    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository memberRepo;
    private final TicketRepository ticketRepo;
    private final TaskRepository taskRepo;

    @Override
    public MyWorkSummaryDTO getEmployeeDashboard(Long organisationId, Long employeeId) {
        Employee emp = empRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 🔹 Projects
        List<Project> projects = projectRepo.findProjectsByEmployeeInvolvement(organisationId, employeeId);
        List<ProjectSummaryDTO> projectSummaries = projects.stream()
                .map(p -> ProjectSummaryDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .status(p.getStatus().name())
                        .priority(p.getPriority().name())
                        .endDate(p.getEndDate())
                        .build())
                .collect(Collectors.toList());

        // 🔹 Tickets
        List<Ticket> tickets = ticketRepo.findByOrganisationIdAndAssignees_IdOrCreatedBy_Id(
                organisationId, employeeId);
        List<TicketSummaryDTO> ticketSummaries = tickets.stream()
                .map(t -> TicketSummaryDTO.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .status(t.getStatus().name())
                        .endDate(t.getEndDate())
                        .projectName(t.getProject() != null ? t.getProject().getName() : "Independent")
                        .build())
                .collect(Collectors.toList());

        List<Task> tasks = taskRepo.findByOrganisationIdAndAssignee_IdOrReporter_Id(organisationId, employeeId);

        List<TaskSummaryDTO> taskSummaries = tasks
        .stream()
        .map((Task t) -> TaskSummaryDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")
                .projectName(t.getProject() != null ? t.getProject().getName() : "Independent")
                .build())
        .collect(Collectors.toList());


        


        // 🔹 Stats
        DashboardProjectStats stats = DashboardProjectStats.builder()
                .totalProjects(projectSummaries.size())
                .activeTickets(tickets.stream()
                        .filter(t -> !t.getStatus().equals(Ticket.TicketStatus.COMPLETED))
                        .count())
                .completedTickets(tickets.stream()
                        .filter(t -> t.getStatus().equals(Ticket.TicketStatus.COMPLETED))
                        .count())
                .pendingTasks(tasks.stream()
                        .filter(t -> !t.getStatus().equals(Task.TaskStatus.DONE))
                        .count())
                .completedTasks(tasks.stream()
                        .filter(t -> t.getStatus().equals(Task.TaskStatus.DONE))
                        .count())
                .build();

        return MyWorkSummaryDTO.builder()
                .employeeId(emp.getId())
                .employeeName(emp.getFirstName() + " " + emp.getLastName())
                .projects(projectSummaries)
                .tickets(ticketSummaries)
                .tasks(taskSummaries)
                .stats(stats)
                .build();
    }
}
