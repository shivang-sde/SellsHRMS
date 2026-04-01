package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.dashboard.*;
import com.sellspark.SellsHRMS.dto.project.ReminderDTO;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.DashboardProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

                // Load raw entities
                List<Project> projects = projectRepo.findProjectsByEmployeeInvolvement(organisationId, employeeId);
                List<Task> tasks = taskRepo.findByOrganisationIdAndAssignee_IdOrReporter_Id(organisationId, employeeId);
                List<Ticket> tickets = ticketRepo.findByOrganisationIdAndAssignees_IdOrCreatedBy_Id(organisationId,
                                employeeId);

                // --- Projects ---
                List<ProjectSummaryDTO> projectSummaries = projects.stream()
                                .<ProjectSummaryDTO>map(p -> ProjectSummaryDTO.builder()
                                                .id(p.getId())
                                                .name(p.getName())
                                                .status(p.getStatus() != null ? p.getStatus().name() : "UNKNOWN")
                                                .priority(p.getPriority() != null ? p.getPriority().name() : "MEDIUM")
                                                .endDate(p.getEndDate())
                                                // .progress(p.getProgress() != null ? p.getProgress() : 0.0) no files
                                                // exits in project
                                                .build())
                                .collect(Collectors.toList());

                // --- Tasks ---
                List<TaskSummaryDTO> taskSummaries = tasks.stream()
                                .<TaskSummaryDTO>map(t -> TaskSummaryDTO.builder()
                                                .id(t.getId())
                                                .title(t.getTitle())
                                                .description(t.getDescription())
                                                .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")

                                                .reminderAt(t.getReminderAt())
                                                .projectName(t.getProject() != null ? t.getProject().getName()
                                                                : "Independent")
                                                .build())
                                .collect(Collectors.toList());

                List<TicketSummaryDTO> ticketSummaries = tickets.stream()
                                .<TicketSummaryDTO>map(t -> TicketSummaryDTO.builder()
                                                .id(t.getId())
                                                .title(t.getTitle())
                                                .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")
                                                // .priority(t.getPriority() != null ? t.getPriority().name() :
                                                // "MEDIUM") not priority variable exist in task entity
                                                .startDate(t.getStartDate())
                                                .endDate(t.getEndDate())
                                                .projectName(t.getProject() != null ? t.getProject().getName()
                                                                : "Independent")
                                                .build())
                                .collect(Collectors.toList());

                // --- Upcoming reminders (next 3 days) ---
                LocalDate today = LocalDate.now();
                LocalDate next3Days = today.plusDays(3);

                Stream<ReminderDTO> taskReminders = tasks.stream()
                                .filter(t -> t.getReminderAt() != null)
                                .filter(t -> {
                                        LocalDate remDate = t.getReminderAt().toLocalDate();
                                        return !remDate.isBefore(today) && !remDate.isAfter(next3Days);
                                })
                                .map(t -> ReminderDTO.builder()
                                                .id(t.getId())
                                                .title(t.getTitle())
                                                .type("TASK")
                                                .projectName(t.getProject() != null ? t.getProject().getName()
                                                                : "Independent")
                                                .dueAt(t.getReminderAt())
                                                .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")
                                                .build());

                Stream<ReminderDTO> ticketReminders = tickets.stream()
                                .filter(t -> t.getEndDate() != null)
                                .filter(t -> {
                                        LocalDate end = t.getEndDate();
                                        return !end.isBefore(today) && !end.isAfter(next3Days);
                                })
                                .map(t -> ReminderDTO.builder()
                                                .id(t.getId())
                                                .title(t.getTitle())
                                                .type("TICKET")
                                                .projectName(t.getProject() != null ? t.getProject().getName()
                                                                : "Independent")
                                                .dueAt(t.getEndDate().atStartOfDay()) // convert LocalDate ->
                                                                                      // LocalDateTime
                                                .status(t.getStatus() != null ? t.getStatus().name() : "UNKNOWN")
                                                .build());

                List<ReminderDTO> upcoming = Stream.concat(taskReminders, ticketReminders)
                                .collect(Collectors.toList());

                // --- Stats ---
                long overdueTasks = tasks.stream()
                                .filter(t -> t.getReminderAt() != null)
                                .filter(t -> t.getReminderAt().toLocalDate().isBefore(today))
                                .filter(t -> t.getStatus() == null || !t.getStatus().equals(Task.TaskStatus.DONE))
                                .count();

                long overdueTickets = tickets.stream()
                                .filter(t -> t.getEndDate() != null)
                                .filter(t -> t.getEndDate().isBefore(today))
                                .filter(t -> t.getStatus() == null
                                                || !t.getStatus().equals(Ticket.TicketStatus.COMPLETED))
                                .count();

                long completedTaskCount = tasks.stream()
                                .filter(t -> t.getStatus() != null && t.getStatus().equals(Task.TaskStatus.DONE))
                                .count();

                long completedTicketCount = tickets.stream()
                                .filter(t -> t.getStatus() != null
                                                && t.getStatus().equals(Ticket.TicketStatus.COMPLETED))
                                .count();

                long pendingTasks = tasks.stream()
                                .filter(t -> t.getStatus() == null || !t.getStatus().equals(Task.TaskStatus.DONE))
                                .count();

                double completionRate = (tasks.size() + tickets.size()) == 0 ? 0.0
                                : (double) (completedTaskCount + completedTicketCount) / (tasks.size() + tickets.size())
                                                * 100.0;

                DashboardProjectStats stats = DashboardProjectStats.builder()
                                .totalProjects(projectSummaries.size())
                                .activeTickets(tickets.stream()
                                                .filter(t -> t.getStatus() == null
                                                                || !t.getStatus().equals(Ticket.TicketStatus.COMPLETED))
                                                .count())
                                .completedTickets(completedTicketCount)
                                .pendingTasks(pendingTasks)
                                .completedTasks(completedTaskCount)
                                // If you extended DashboardProjectStats to include overdue/activeProjects etc,
                                // set them here.
                                .build();

                // --- Build summary DTO ---
                return MyWorkSummaryDTO.builder()
                                .employeeId(emp.getId())
                                .employeeName((emp.getFirstName() != null ? emp.getFirstName() : "") + " "
                                                + (emp.getLastName() != null ? emp.getLastName() : ""))
                                .activeProjects(projectSummaries)
                                .assignedTasks(taskSummaries)
                                .assignedTickets(ticketSummaries)
                                .upcomingDeadlines(upcoming)
                                .stats(stats)
                                .build();

        }

}
