// package com.sellspark.SellsHRMS.seeder;

// import com.sellspark.SellsHRMS.entity.*;
// import com.sellspark.SellsHRMS.entity.AttendanceSummary.AttendanceStatus;
// import com.sellspark.SellsHRMS.entity.AttendanceSummary.AttendanceSource;
// import com.sellspark.SellsHRMS.entity.PunchInOut.Source;
// import com.sellspark.SellsHRMS.repository.*;
// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// import java.time.*;
// import java.util.*;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class AttendanceTestDataSeeder {

//     private final OrganisationRepository organisationRepository;
//     private final EmployeeRepository employeeRepository;
//     private final PunchInOutRepository punchRepository;
//     private final AttendanceSummaryRepository attendanceRepository;

//     private static final Long ORG_ID = 7L;
//     private static final int YEAR = 2026;
//     private static final int MONTH = 1; // January

//     @PostConstruct
//     public void populateAttendanceData() {
//         log.info("🚀 Starting Attendance Seeder for Org ID: {} for {}/{}", ORG_ID, MONTH, YEAR);

//         Organisation org = organisationRepository.findById(ORG_ID)
//                 .orElseThrow(() -> new RuntimeException("❌ Organisation not found for ID: " + ORG_ID));

//         List<Employee> employees = employeeRepository.findByOrganisationIdAndStatus(org.getId(), Employee.EmployeeStatus.ACTIVE);
//         if (employees.isEmpty()) {
//             log.warn("⚠️ No active employees found for organisation ID {}", ORG_ID);
//             return;
//         }

//         // 🧹 Step 1: Clean up existing data for the month
//         LocalDate startDate = LocalDate.of(YEAR, MONTH, 1);
//         LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

//         log.info("🧹 Cleaning existing attendance + punch data for {} to {}", startDate, endDate);
//         int deletedAttendance = attendanceRepository.deleteByOrganisationAndDateRange(org.getId(), startDate, endDate);
//         int deletedPunch = punchRepository.deleteByOrganisationAndDateRange(org.getId(), startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
//         log.info("🗑️ Deleted {} attendance and {} punch records", deletedAttendance, deletedPunch);

//         // 🏗️ Step 2: Populate fresh test data
//         Random random = new Random();
//         int totalRecords = 0;

//         for (Employee emp : employees) {
//             log.info("👤 Generating attendance for Employee ID: {} ({})", emp.getId(), emp.getFirstName());
//             for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

//                 // Sundays = WEEK_OFF
//                 if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                     attendanceRepository.save(AttendanceSummary.builder()
//                             .organisation(org)
//                             .employee(emp)
//                             .attendanceDate(date)
//                             .status(AttendanceStatus.WEEK_OFF)
//                             .source(AttendanceSource.AUTO_SYSTEM)
//                             .remarks("Sunday weekly off")
//                             .build());
//                     log.debug("📅 {} → WEEK_OFF", date);
//                     totalRecords++;
//                     continue;
//                 }

//                 int randomFlag = random.nextInt(20);
//                 if (randomFlag < 2) {
//                     // 10% ON_LEAVE
//                     attendanceRepository.save(AttendanceSummary.builder()
//                             .organisation(org)
//                             .employee(emp)
//                             .attendanceDate(date)
//                             .status(AttendanceStatus.ON_LEAVE)
//                             .source(AttendanceSource.LEAVE_SYSTEM)
//                             .remarks("Auto test leave")
//                             .build());
//                     log.debug("📅 {} → ON_LEAVE", date);
//                 } else if (randomFlag < 4) {
//                     // 10% ABSENT
//                     attendanceRepository.save(AttendanceSummary.builder()
//                             .organisation(org)
//                             .employee(emp)
//                             .attendanceDate(date)
//                             .status(AttendanceStatus.ABSENT)
//                             .source(AttendanceSource.AUTO_SYSTEM)
//                             .remarks("Absent for test")
//                             .build());
//                     log.debug("📅 {} → ABSENT", date);
//                 } else {
//                     // PRESENT + Punch Record
//                     LocalDateTime in = date.atTime(9 + random.nextInt(2), random.nextInt(59));
//                     LocalDateTime out = date.atTime(17 + random.nextInt(2), random.nextInt(59));
//                     double workHours = Duration.between(in, out).toHours() + random.nextDouble();

//                     PunchInOut punch = punchRepository.save(PunchInOut.builder()
//                             .organisation(org)
//                             .employee(emp)
//                             .punchIn(in)
//                             .punchOut(out)
//                             .workHours(workHours)
//                             .punchSource(Source.BIOMETRIC)
//                             .isAutoPunchGenerated(false)
//                             .location("HQ Office")
//                             .build());

//                     attendanceRepository.save(AttendanceSummary.builder()
//                             .organisation(org)
//                             .employee(emp)
//                             .attendanceDate(date)
//                             .status(AttendanceStatus.PRESENT)
//                             .punchRecord(punch)
//                             .effectivePunchIn(in)
//                             .effectivePunchOut(out)
//                             .workHours(workHours)
//                             .overtimeHours(workHours > 9 ? workHours - 9 : 0)
//                             .isLate(in.getHour() > 9)
//                             .isEarlyOut(out.getHour() < 17)
//                             .source(AttendanceSource.PUNCH_SYSTEM)
//                             .remarks("Auto-generated attendance for payroll test")
//                             .build());
//                     log.debug("📅 {} → PRESENT ({} hrs)", date, String.format("%.2f", workHours));
//                 }

//                 totalRecords++;
//             }
//         }

//         log.info("✅ Seeder completed successfully for Org ID {} — Total Records Created: {}", ORG_ID, totalRecords);
//     }
// }
