package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final OrganisationRepository organisationRepo;
    private final OrganisationPolicyRepository policyRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceSummaryRepository summaryRepo;
    private final HolidayRepository holidayRepo;
    private final LeaveRepository leaveRepo;
    private final PunchInOutRepository punchRepo;

    /**
     * PHASE 1: PRE-MARK ATTENDANCE
     * Runs every day at midnight (00:00:00)
     * Creates attendance summary records for all active employees
     */
    @Scheduled(cron = "10 17 0 * * ?")
    @Transactional
    public void preMarkDailyAttendance() {
        log.info("========== PRE-MARKING ATTENDANCE - STARTED ==========");
        LocalDate today = LocalDate.now();

        // Get all active organisations
        List<Organisation> organisations = organisationRepo.findAll()
                .stream()
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .toList();

        int totalMarked = 0;

        for (Organisation org : organisations) {
            log.info("Processing organisation: {} (ID: {})", org.getName(), org.getId());

            // Get all active employees for this organisation
            List<Employee> employees = employeeRepo.findByOrganisationIdAndDeletedFalse(
                    org.getId()).stream()
                    .filter(emp -> emp.getStatus() == Employee.EmployeeStatus.ACTIVE)
                    .toList();

            for (Employee employee : employees) {
                try {
                    // Check if record already exists (avoid duplicates)
                    if (summaryRepo.findByEmployeeIdAndAttendanceDate(employee.getId(), today)
                            .isPresent()) {
                        log.debug("Attendance already marked for employee: {}", employee.getId());
                        continue;
                    }

                    AttendanceSummary.AttendanceStatus status;
                    String remarks;

                    // Check if today is a holiday
                    boolean isHoliday = holidayRepo.existsByOrganisationIdAndHolidayDate(
                            org.getId(),
                            today);

                    // Check if today is a week off (Saturday/Sunday by default)
                    boolean isWeekOff = isWeekOff(today); // cross check by sir to get week off date clearance(emp based
                                                          // or fiexed)

                    // Check if employee has approved leave
                    boolean hasLeave = leaveRepo.existsByEmployeeAndDateAndApproved(
                            employee.getId(),
                            today);

                    // Determine initial status
                    if (isHoliday) {
                        status = AttendanceSummary.AttendanceStatus.HOLIDAY;
                        remarks = "Public/Company Holiday";
                    } else if (isWeekOff) {
                        status = AttendanceSummary.AttendanceStatus.WEEK_OFF;
                        remarks = "Weekly Off";
                    } else if (hasLeave) {
                        status = AttendanceSummary.AttendanceStatus.ON_LEAVE;
                        remarks = "Approved Leave";
                    } else {
                        // Pre-mark as ABSENT (will be updated when employee punches in)
                        status = AttendanceSummary.AttendanceStatus.ABSENT;
                        remarks = "Tentative - Awaiting punch in";
                    }

                    // Create attendance summary
                    AttendanceSummary summary = AttendanceSummary.builder()
                            .organisation(org)
                            .employee(employee)
                            .attendanceDate(today)
                            .status(status)
                            .source(AttendanceSummary.AttendanceSource.AUTO_SYSTEM)
                            .remarks(remarks)
                            .build();

                    summaryRepo.save(summary);
                    totalMarked++;

                } catch (Exception e) {
                    log.error("Error pre-marking attendance for employee {}: {}",
                            employee.getId(), e.getMessage());
                }
            }

            log.info("Marked attendance for {} employees in org: {}",
                    employees.size(), org.getName());
        }

        log.info("========== PRE-MARKING ATTENDANCE - COMPLETED ==========");
        log.info("Total records created: {}", totalMarked);
    }

    /**
     * PHASE 2: RECONCILIATION
     * Runs every day at 11:59 PM (23:59:00)
     * Finalizes attendance - auto punch-out, confirm absences
     */
    @Scheduled(cron = "0 50 23 * * ?")
    @Transactional
    public void reconcileDailyAttendance() {
        log.info("========== RECONCILIATION - STARTED ==========");
        LocalDate today = LocalDate.now();

        // Get all attendance summaries for today
        List<AttendanceSummary> summaries = summaryRepo.findByAttendanceDate(today);

        int autoCheckedOut = 0;
        int finalizedAbsent = 0;

        for (AttendanceSummary summary : summaries) {
            try {
                // Skip already finalized statuses
                if (summary.getStatus() == AttendanceSummary.AttendanceStatus.HOLIDAY ||
                        summary.getStatus() == AttendanceSummary.AttendanceStatus.WEEK_OFF ||
                        summary.getStatus() == AttendanceSummary.AttendanceStatus.ON_LEAVE) {
                    continue;
                }

                // Case 1: Still marked ABSENT (never punched in)
                if (summary.getStatus() == AttendanceSummary.AttendanceStatus.ABSENT) {
                    summary.setRemarks("Absent - No punch record");
                    summaryRepo.save(summary);
                    finalizedAbsent++;
                    continue;
                }

                // Case 2: Punched IN but forgot to punch OUT
                if (summary.getStatus() == AttendanceSummary.AttendanceStatus.PRESENT &&
                        summary.getEffectivePunchOut() == null &&
                        summary.getPunchRecord() != null) {

                    PunchInOut punch = summary.getPunchRecord();

                    // Auto punch out after 10 hours
                    Instant autoPunchOut = punch.getPunchIn();
                    punch.setPunchOut(autoPunchOut);

                    // Calculate work hours
                    Duration duration = Duration.between(punch.getPunchIn(), autoPunchOut);
                    double hours = duration.toMinutes() / 60.0;
                    punch.setWorkHours(hours);
                    punchRepo.save(punch);

                    // Update summary
                    summary.setEffectivePunchOut(autoPunchOut);
                    summary.setWorkHours(hours);
                    summary.setRemarks("Auto punched out after 10 hours");
                    summaryRepo.save(summary);

                    autoCheckedOut++;
                    log.info("Auto checked out employee: {} at {}",
                            summary.getEmployee().getId(), autoPunchOut);
                }

            } catch (Exception e) {
                log.error("Error reconciling attendance for summary {}: {}",
                        summary.getId(), e.getMessage());
            }
        }

        log.info("========== RECONCILIATION - COMPLETED ==========");
        log.info("Auto checked out: {}", autoCheckedOut);
        log.info("Finalized absent: {}", finalizedAbsent);
    }

    /**
     * Helper method to check if a date is a week off
     * TODO: Make this configurable per organisation/employee
     */
    private boolean isWeekOff(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        // Default: Saturday and Sunday are week offs
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}