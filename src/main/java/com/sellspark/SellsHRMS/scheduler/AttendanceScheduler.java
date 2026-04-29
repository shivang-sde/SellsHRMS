package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

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

    // ================= PRE-MARK =================

    @Scheduled(cron = "0 29 1 * * ?", zone = "Asia/Kolkata")
    @Transactional
    public void preMarkDailyAttendance() {

        log.info("========== PRE-MARK START ==========");
        LocalDate today = LocalDate.now();

        List<Organisation> orgs = organisationRepo.findAll()
                .stream()
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .toList();

        for (Organisation org : orgs) {

            OrganisationPolicy policy = policyRepo
                    .findByOrganisationId(org.getId())
                    .orElse(null);

            if (policy == null) {
                log.error("Policy missing for org: {}", org.getId());
                continue;
            }

            List<Employee> employees = employeeRepo
                    .findByOrganisationIdAndDeletedFalse(org.getId())
                    .stream()
                    .filter(emp -> emp.getStatus() == Employee.EmployeeStatus.ACTIVE)
                    .toList();

            for (Employee emp : employees) {
                try {

                    if (summaryRepo.findByEmployeeIdAndAttendanceDate(emp.getId(), today).isPresent()) {
                        continue;
                    }

                    boolean isHoliday = holidayRepo
                            .existsByOrganisationIdAndHolidayDate(org.getId(), today);

                    boolean isWeekOff = isWeekOff(today, policy);

                    boolean isLeave = leaveRepo
                            .existsByEmployeeAndDateAndApproved(emp.getId(), today);

                    Optional<Leave> leave = leaveRepo.findApprovedLeaveByEmployeeAndDate(emp.getId(), today);

                    AttendanceSummary.AttendanceStatus status;
                    String remarks;

                    if (isHoliday) {
                        status = AttendanceSummary.AttendanceStatus.HOLIDAY;
                        remarks = "Holiday";
                    } else if (isWeekOff) {
                        status = AttendanceSummary.AttendanceStatus.WEEK_OFF;
                        remarks = "Week Off (Policy)";

                    } else if (isLeave) {
                        status = AttendanceSummary.AttendanceStatus.ON_LEAVE;
                        remarks = "On Leave";

                    } else {
                        status = AttendanceSummary.AttendanceStatus.ABSENT;
                        remarks = "Awaiting punch (Policy)";
                    }

                    summaryRepo.save(
                            AttendanceSummary.builder()
                                    .organisation(org)
                                    .employee(emp)
                                    .attendanceDate(today)
                                    .status(status)
                                    .leave(leave.orElse(null))
                                    .source(AttendanceSummary.AttendanceSource.AUTO_SYSTEM)
                                    .remarks(remarks)
                                    .build());

                } catch (Exception e) {
                    log.error("Error pre-mark emp {}: {}", emp.getId(), e.getMessage());
                }
            }
        }

        log.info("========== PRE-MARK END ==========");
    }

    // ================= RECONCILE =================

    @Scheduled(cron = "0 50 23 * * ?", zone = "Asia/Kolkata")
    @Transactional
    public void reconcileDailyAttendance() {

        log.info("========== RECONCILE START ==========");

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        List<AttendanceSummary> summaries = summaryRepo.findByAttendanceDate(today);

        for (AttendanceSummary summary : summaries) {
            try {

                Organisation org = summary.getOrganisation();

                OrganisationPolicy policy = policyRepo
                        .findByOrganisationId(org.getId())
                        .orElse(null);

                if (policy == null || policy.getAutoPunchOutTime() == null) {
                    log.warn("Skipping org {} due to missing policy", org.getId());
                    continue;
                }

                // Skip fixed statuses
                if (summary.getStatus() == AttendanceSummary.AttendanceStatus.HOLIDAY ||
                        summary.getStatus() == AttendanceSummary.AttendanceStatus.WEEK_OFF ||
                        summary.getStatus() == AttendanceSummary.AttendanceStatus.ON_LEAVE) {
                    continue;
                }

                // If no punch at all → remain ABSENT
                if (summary.getPunchRecord() == null) {
                    summary.setRemarks("Absent - No punch");
                    summaryRepo.save(summary);
                    continue;
                }

                // Skip if already punched out (idempotent)
                if (summary.getEffectivePunchOut() != null) {
                    continue;
                }

                // ================= AUTO PUNCH-OUT =================

                PunchInOut punch = summary.getPunchRecord();
                ZoneId zoneId = ZoneId.of(org.getTimeZone());
                LocalTime autoTime = policy.getAutoPunchOutTime();

                // Optional grace (0 if not configured)
                int graceMinutes = Optional.ofNullable(policy.getLateGraceMinutes()).orElse(0);
                LocalTime finalAutoTime = autoTime.plusMinutes(graceMinutes);

                Instant autoPunchOut = LocalDateTime.of(today, finalAutoTime)
                        .atZone(zoneId)
                        .toInstant();

                // Calculate hours (no validation, no fallback)
                Duration duration = Duration.between(punch.getPunchIn(), autoPunchOut);
                double hours = Math.max(0, duration.toMinutes() / 60.0);

                // Round for payroll consistency
                hours = Math.round(hours * 100.0) / 100.0;

                double fullDay = Optional.ofNullable(policy.getStandardDailyHours()).orElse(8.0);
                double halfDay = fullDay / 2;

                // Decide attendance status
                if (hours >= fullDay) {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
                } else if (hours >= halfDay) {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.HALF_DAY);
                } else {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.SHORT_DAY);
                }

                // Save punch
                punch.setPunchOut(autoPunchOut);
                punch.setWorkHours(hours);
                punchRepo.save(punch);

                // Update summary
                summary.setEffectivePunchOut(autoPunchOut);
                summary.setWorkHours(hours);
                summary.setRemarks("Auto punched out (Policy: " + finalAutoTime + ")");
                summaryRepo.save(summary);

            } catch (Exception e) {
                log.error("Error reconcile summary {}: {}", summary.getId(), e.getMessage());
            }
        }

        log.info("========== RECONCILE END ==========");
    }

    // ================= POLICY WEEK-OFF =================

    private boolean isWeekOff(LocalDate date, OrganisationPolicy policy) {

        if (policy.getWeekOffDays() == null || policy.getWeekOffDays().isEmpty()) {
            return false;
        }

        return policy.getWeekOffDays().contains(date.getDayOfWeek());
    }
}