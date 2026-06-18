package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.notification.dto.EmailRequestDTO;
import com.sellspark.SellsHRMS.notification.enums.EmailType;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import com.sellspark.SellsHRMS.notification.factory.MailSenderFactory;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.AttendanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceReportScheduler {

    private static final Long ROOT_ORG_ID = 1L;

    private final OrganisationRepository organisationRepo;
    private final OrganisationAdminRepository orgAdminRepo;
    private final AttendanceService attendanceService;
    private final MailSenderFactory mailSenderFactory;

    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Kolkata")
    @Transactional(readOnly = true)
    public void sendDailyAttendanceReport() {
        log.info("========== DAILY ATTENDANCE REPORT SCHEDULER START ==========");

        LocalDate reportDate = LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1);
        log.info("Generating reports for date: {}", reportDate);

        List<Organisation> orgs = organisationRepo.findAll()
                .stream()
                .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
                .filter(org -> !ROOT_ORG_ID.equals(org.getId()))
                .toList();

        log.info("Found {} active non-root organizations to process.", orgs.size());

        for (Organisation org : orgs) {
            try {
                // Fetch admin email from repository
                OrganisationAdmin admin = orgAdminRepo.findByOrganisation_Id(org.getId());
                if (admin == null || admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
                    log.warn("Skipping organization ID: {} (Name: {}) - Admin email not found.", org.getId(), org.getName());
                    continue;
                }

                List<PunchRecordResponse> records = attendanceService.getOrgAttendanceByDateSystem(org.getId(), reportDate);
                log.info("Org: {} - Fetched {} attendance records.", org.getName(), records.size());

                String htmlReport = generateHtmlReport(org, reportDate, records);

                EmailRequestDTO request = EmailRequestDTO.builder()
                        .orgId(org.getId())
                        .eventCode("DAILY_ATTENDANCE_REPORT")
                        .targetRole(TargetRole.ADMIN)
                        .toEmail(admin.getEmail())
                        .toName(admin.getFullName() != null ? admin.getFullName() : "Admin")
                        .subject("Daily Attendance Report - " + reportDate)
                        .body(htmlReport)
                        .emailType(EmailType.SYSTEM)
                        .build();

                mailSenderFactory.sendEmail(request);
                log.info("Report email sent successfully to: {} for organization: {}", admin.getEmail(), org.getName());

            } catch (Exception e) {
                log.error("Failed to generate/send attendance report for organization ID: {} - Error: {}", org.getId(), e.getMessage(), e);
            }
        }

        log.info("========== DAILY ATTENDANCE REPORT SCHEDULER END ==========");
    }

    private String generateHtmlReport(Organisation org, LocalDate reportDate, List<PunchRecordResponse> records) {
        long totalEmployees = records.size();
        long presentCount = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
        long absentCount = records.stream().filter(r -> "ABSENT".equalsIgnoreCase(r.getStatus())).count();
        long halfDayCount = records.stream().filter(r -> "HALF_DAY".equalsIgnoreCase(r.getStatus())).count();
        long shortDayCount = records.stream().filter(r -> "SHORT_DAY".equalsIgnoreCase(r.getStatus())).count();
        long onLeaveCount = records.stream().filter(r -> "ON_LEAVE".equalsIgnoreCase(r.getStatus())).count();
        long lateCount = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsLate())).count();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Daily Attendance Report</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; color: #333333; margin: 0; padding: 0; -webkit-font-smoothing: antialiased; }");
        html.append(".container { max-width: 900px; margin: 30px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e1e8ed; }");
        html.append(".header { background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%); color: #ffffff; padding: 30px 40px; text-align: left; }");
        html.append(".header h1 { margin: 0; font-size: 26px; font-weight: 600; letter-spacing: -0.5px; }");
        html.append(".header p { margin: 5px 0 0 0; font-size: 14px; opacity: 0.85; }");
        html.append(".content { padding: 40px; }");
        html.append(".section-title { font-size: 18px; font-weight: 600; color: #1e3c72; margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #eef2f5; padding-bottom: 8px; }");
        
        // Stats Styling
        html.append("table.stats-table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }");
        html.append("table.stats-table td { padding: 10px; width: 14%; }");
        html.append(".stat-card { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 15px; text-align: center; }");
        html.append(".stat-card .value { font-size: 22px; font-weight: 700; margin-bottom: 4px; }");
        html.append(".stat-card .label { font-size: 11px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; }");
        html.append(".card-total { border-top: 4px solid #64748b; } .card-total .value { color: #334155; }");
        html.append(".card-present { border-top: 4px solid #10b981; } .card-present .value { color: #047857; }");
        html.append(".card-absent { border-top: 4px solid #ef4444; } .card-absent .value { color: #b91c1c; }");
        html.append(".card-half { border-top: 4px solid #f59e0b; } .card-half .value { color: #b45309; }");
        html.append(".card-short { border-top: 4px solid #ec4899; } .card-short .value { color: #be185d; }");
        html.append(".card-leave { border-top: 4px solid #3b82f6; } .card-leave .value { color: #1d4ed8; }");
        html.append(".card-late { border-top: 4px solid #8b5cf6; } .card-late .value { color: #6d28d9; }");
        
        // Data Table Styling
        html.append("table.data-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 13px; }");
        html.append("table.data-table th { background-color: #f1f5f9; color: #475569; font-weight: 600; padding: 12px 14px; border-bottom: 2px solid #e2e8f0; text-transform: uppercase; font-size: 11px; letter-spacing: 0.5px; }");
        html.append("table.data-table td { padding: 12px 14px; border-bottom: 1px solid #e2e8f0; color: #334155; vertical-align: middle; }");
        html.append("table.data-table tr:nth-child(even) { background-color: #fafbfc; }");
        
        // Badges
        html.append(".status-badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: 600; text-align: center; text-transform: uppercase; }");
        html.append(".status-present { background-color: #d1fae5; color: #065f46; }");
        html.append(".status-absent { background-color: #fee2e2; color: #991b1b; }");
        html.append(".status-half_day { background-color: #fef3c7; color: #92400e; }");
        html.append(".status-short_day { background-color: #fce7f3; color: #9d174d; }");
        html.append(".status-on_leave { background-color: #dbeafe; color: #1e40af; }");
        html.append(".status-week_off { background-color: #f1f5f9; color: #475569; }");
        html.append(".status-holiday { background-color: #e0f2fe; color: #075985; }");
        html.append(".status-default { background-color: #f3f4f6; color: #374151; }");
        html.append(".flag-yes { color: #b91c1c; font-weight: 600; }");
        html.append(".flag-no { color: #94a3b8; }");
        
        html.append(".footer { background-color: #f8fafc; padding: 20px 40px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; }");
        html.append("</style></head><body>");

        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>Daily Attendance Report</h1>");
        html.append("<p>Organization: ").append(org.getName()).append(" | Date: ").append(reportDate).append("</p>");
        html.append("</div>");

        html.append("<div class='content'>");
        
        // Summary Table
        html.append("<h2 class='section-title'>Attendance Summary</h2>");
        html.append("<table class='stats-table'><tr>");
        html.append("<td><div class='stat-card card-total'><div class='value'>").append(totalEmployees).append("</div><div class='label'>Total</div></div></td>");
        html.append("<td><div class='stat-card card-present'><div class='value'>").append(presentCount).append("</div><div class='label'>Present</div></div></td>");
        html.append("<td><div class='stat-card card-absent'><div class='value'>").append(absentCount).append("</div><div class='label'>Absent</div></div></td>");
        html.append("<td><div class='stat-card card-half'><div class='value'>").append(halfDayCount).append("</div><div class='label'>Half Day</div></div></td>");
        html.append("<td><div class='stat-card card-short'><div class='value'>").append(shortDayCount).append("</div><div class='label'>Short Day</div></div></td>");
        html.append("<td><div class='stat-card card-leave'><div class='value'>").append(onLeaveCount).append("</div><div class='label'>On Leave</div></div></td>");
        html.append("<td><div class='stat-card card-late'><div class='value'>").append(lateCount).append("</div><div class='label'>Late</div></div></td>");
        html.append("</tr></table>");

        // Records Table
        html.append("<h2 class='section-title'>Attendance Records</h2>");
        html.append("<table class='data-table'>");
        html.append("<thead><tr>");
        html.append("<th>Employee Name</th>");
        html.append("<th>Employee Code</th>");
        html.append("<th>Department</th>");
        html.append("<th>Punch In</th>");
        html.append("<th>Punch Out</th>");
        html.append("<th>Work Hours</th>");
        html.append("<th>Status</th>");
        html.append("<th>Late</th>");
        html.append("<th>Early Out</th>");
        html.append("<th>Remarks</th>");
        html.append("</tr></thead><tbody>");

        if (records.isEmpty()) {
            html.append("<tr><td colspan='10' style='text-align: center; color: #94a3b8; padding: 30px;'>No attendance records found for this day.</td></tr>");
        } else {
            for (PunchRecordResponse record : records) {
                html.append("<tr>");
                html.append("<td>").append(record.getEmployeeName() != null ? record.getEmployeeName() : "N/A").append("</td>");
                html.append("<td>").append(record.getEmployeeCode() != null ? record.getEmployeeCode() : "N/A").append("</td>");
                html.append("<td>").append(record.getDepartment() != null ? record.getDepartment() : "N/A").append("</td>");
                html.append("<td>").append(formatTime(record.getPunchIn())).append("</td>");
                html.append("<td>").append(formatTime(record.getPunchOut())).append("</td>");
                html.append("<td>").append(formatWorkHours(record.getWorkHours())).append("</td>");
                
                // Status Badge
                String status = record.getStatus() != null ? record.getStatus() : "UNKNOWN";
                String badgeClass = getBadgeClass(status);
                html.append("<td><span class='status-badge ").append(badgeClass).append("'>").append(status).append("</span></td>");
                
                // Late / Early Out Flags
                String lateFlag = Boolean.TRUE.equals(record.getIsLate()) ? "<span class='flag-yes'>Yes</span>" : "<span class='flag-no'>No</span>";
                String earlyFlag = Boolean.TRUE.equals(record.getIsEarlyOut()) ? "<span class='flag-yes'>Yes</span>" : "<span class='flag-no'>No</span>";
                html.append("<td>").append(lateFlag).append("</td>");
                html.append("<td>").append(earlyFlag).append("</td>");
                
                html.append("<td>").append(record.getRemarks() != null ? record.getRemarks() : "No remarks").append("</td>");
                html.append("</tr>");
            }
        }

        html.append("</tbody></table>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>This is an automated operational report generated by Sellspark HRMS.</p>");
        html.append("</div>");
        html.append("</div></body></html>");

        return html.toString();
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private String formatWorkHours(Double hours) {
        if (hours == null || hours == 0.0) {
            return "0.00";
        }
        return String.format("%.2f", hours);
    }

    private String getBadgeClass(String status) {
        switch (status.toUpperCase()) {
            case "PRESENT": return "status-present";
            case "ABSENT": return "status-absent";
            case "HALF_DAY": return "status-half_day";
            case "SHORT_DAY": return "status-short_day";
            case "ON_LEAVE": return "status-on_leave";
            case "WEEK_OFF": return "status-week_off";
            case "HOLIDAY": return "status-holiday";
            default: return "status-default";
        }
    }
}
