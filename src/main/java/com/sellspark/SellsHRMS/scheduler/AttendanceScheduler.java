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

    private static final Long ROOT_ORG_ID = 1L;

    private final OrganisationRepository organisationRepo;
    private final OrganisationPolicyRepository policyRepo;
    private final EmployeeRepository employeeRepo;
    private final AttendanceSummaryRepository summaryRepo;
    private final HolidayRepository holidayRepo;
    private final LeaveRepository leaveRepo;
    private final PunchInOutRepository punchRepo;

    // ================= PRE-MARK =================

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void preMarkDailyAttendance() {

        log.info("========== PRE-MARK START ==========");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        List<Organisation> orgs = organisationRepo.findAll()
                .stream()
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .filter(org -> !ROOT_ORG_ID.equals(org.getId()))
                .toList();

        log.info("Eligible active orgs found: {}", orgs.size());

        for (Organisation org : orgs) {

            try {

                log.info("Processing org {} - {}", org.getId(), org.getName());

                OrganisationPolicy policy = policyRepo
                        .findByOrganisationId(org.getId())
                        .orElse(null);

                if (policy == null) {
                    log.warn("Policy missing for org {}", org.getId());
                    continue;
                }

                List<Employee> employees = employeeRepo
                        .findByOrganisationIdAndDeletedFalse(org.getId())
                        .stream()
                        .filter(emp -> emp.getStatus() == Employee.EmployeeStatus.ACTIVE)
                        .toList();

                log.info("Org {} active employees: {}", org.getId(), employees.size());

                int created = 0;
                int skipped = 0;
                int failed = 0;

                for (Employee emp : employees) {
                    try {

                        if (summaryRepo.findByEmployeeIdAndAttendanceDate(emp.getId(), today).isPresent()) {
                            skipped++;
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

                        created++;

                    } catch (Exception e) {
                        failed++;
                        log.error("Pre-mark failed org {} emp {} : {}",
                                org.getId(), emp.getId(), e.getMessage(), e);
                    }
                }

                log.info(
                        "Org {} completed -> created={}, skipped={}, failed={}",
                        org.getId(), created, skipped, failed);

            } catch (Exception e) {
                log.error("Org level failure {} : {}", org.getId(), e.getMessage(), e);
            }
        }

        log.info("========== PRE-MARK END ==========");
    }

    // ================= RECONCILE =================

    @Scheduled(cron = "0 50 23 * * ?", zone = "Asia/Kolkata")
    @Transactional
    public void reconcileDailyAttendance() {

        log.info("========== RECONCILE START ==========");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        List<AttendanceSummary> summaries = summaryRepo.findByAttendanceDate(today);

        log.info("Attendance summaries found: {}", summaries.size());

        int processed = 0;
        int skipped = 0;
        int failed = 0;

        for (AttendanceSummary summary : summaries) {
            try {

                Organisation org = summary.getOrganisation();

                if (ROOT_ORG_ID.equals(org.getId())) {
                    skipped++;
                    continue;
                }

                OrganisationPolicy policy = policyRepo
                        .findByOrganisationId(org.getId())
                        .orElse(null);

                if (policy == null || policy.getAutoPunchOutTime() == null) {
                    skipped++;
                    log.warn("Skipping org {} due to missing policy", org.getId());
                    continue;
                }

                if (summary.getStatus() == AttendanceSummary.AttendanceStatus.HOLIDAY
                        || summary.getStatus() == AttendanceSummary.AttendanceStatus.WEEK_OFF
                        || summary.getStatus() == AttendanceSummary.AttendanceStatus.ON_LEAVE) {
                    skipped++;
                    continue;
                }

                if (summary.getPunchRecord() == null) {
                    summary.setRemarks("Absent - No punch");
                    summaryRepo.save(summary);
                    processed++;
                    continue;
                }

                if (summary.getEffectivePunchOut() != null) {
                    skipped++;
                    continue;
                }

                PunchInOut punch = summary.getPunchRecord();

                ZoneId zoneId = ZoneId.of(
                        org.getTimeZone() == null ? "Asia/Kolkata" : org.getTimeZone());

                LocalTime autoTime = policy.getAutoPunchOutTime();

                int graceMinutes = Optional.ofNullable(policy.getLateGraceMinutes()).orElse(0);

                LocalTime finalAutoTime = autoTime.plusMinutes(graceMinutes);

                Instant autoPunchOut = LocalDateTime.of(today, finalAutoTime)
                        .atZone(zoneId)
                        .toInstant();

                Duration duration = Duration.between(punch.getPunchIn(), autoPunchOut);

                double hours = Math.max(0, duration.toMinutes() / 60.0);
                hours = Math.round(hours * 100.0) / 100.0;

                double fullDay = Optional.ofNullable(policy.getStandardDailyHours()).orElse(8.0);

                double halfDay = fullDay / 2;

                if (hours >= fullDay) {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.PRESENT);
                } else if (hours >= halfDay) {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.HALF_DAY);
                } else {
                    summary.setStatus(AttendanceSummary.AttendanceStatus.SHORT_DAY);
                }

                punch.setPunchOut(autoPunchOut);
                punch.setWorkHours(hours);
                punchRepo.save(punch);

                summary.setEffectivePunchOut(autoPunchOut);
                summary.setWorkHours(hours);
                summary.setRemarks("Auto punched out (Policy: " + finalAutoTime + ")");
                summaryRepo.save(summary);

                processed++;

            } catch (Exception e) {
                failed++;
                log.error("Reconcile failed summary {} : {}",
                        summary.getId(), e.getMessage(), e);
            }
        }

        log.info(
                "Reconcile completed -> processed={}, skipped={}, failed={}",
                processed, skipped, failed);

        log.info("========== RECONCILE END ==========");
    }

    // ================= WEEK-OFF =================

    private boolean isWeekOff(LocalDate date, OrganisationPolicy policy) {

        if (policy.getWeekOffDays() == null || policy.getWeekOffDays().isEmpty()) {
            return false;
        }

        return policy.getWeekOffDays().contains(date.getDayOfWeek());
    }
}