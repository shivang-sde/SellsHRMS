// package com.sellspark.SellsHRMS.seeder;

// import com.sellspark.SellsHRMS.entity.*;
// import com.sellspark.SellsHRMS.entity.AttendanceSummary.AttendanceSource;
// import com.sellspark.SellsHRMS.entity.AttendanceSummary.AttendanceStatus;
// import com.sellspark.SellsHRMS.entity.PunchInOut.Source;
// import com.sellspark.SellsHRMS.repository.*;
// import jakarta.annotation.PostConstruct;
// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// import java.time.*;
// import java.util.*;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class AttendanceHistorySeeder {

//     private final OrganisationRepository organisationRepo;
//     private final EmployeeRepository employeeRepo;
//     private final AttendanceSummaryRepository attendanceRepo;
//     private final PunchInOutRepository punchRepo;

//     private static final int DAYS_BACK = 60;

//     @Transactional
//     @PostConstruct
//     public void populateAttendanceHistory() {
//         LocalDate endDate = LocalDate.now().minusDays(1);
//         LocalDate startDate = endDate.minusDays(DAYS_BACK - 1);

//         log.info("🚀 Starting full attendance re-seed for {} → {}", startDate, endDate);

//         List<Organisation> orgs = organisationRepo.findByIsActiveTrue();
//         if (orgs.isEmpty()) {
//             log.warn("⚠️ No active organisations found.");
//             return;
//         }

//         Random random = new Random();

//         for (Organisation org : orgs) {
//             log.info("🏢 Organisation: {} (ID: {})", org.getName(), org.getId());

//             List<Employee> employees =
//                     employeeRepo.findByOrganisationIdAndStatus(org.getId(), Employee.EmployeeStatus.ACTIVE);

//             if (employees.isEmpty()) {
//                 log.warn("   ↳ No active employees found for org {}", org.getId());
//                 continue;
//             }

//             // Delete existing attendance + punch data for this organisation before reseeding
//             log.info("   🧹 Cleaning old attendance & punches for org {}", org.getId());
//             attendanceRepo.deleteByOrganisationId(org.getId());
//             punchRepo.deleteByOrganisationId(org.getId());

//             for (Employee emp : employees) {
//                 int created = 0;

//                 for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

//                     // Sundays = WEEK_OFF
//                     if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                         attendanceRepo.save(AttendanceSummary.builder()
//                                 .organisation(org)
//                                 .employee(emp)
//                                 .attendanceDate(date)
//                                 .status(AttendanceStatus.WEEK_OFF)
//                                 .source(AttendanceSource.AUTO_SYSTEM)
//                                 .remarks("Weekly off")
//                                 .build());
//                         continue;
//                     }

//                     int roll = random.nextInt(100);

//                     if (roll < 8) { // ~8% leave
//                         attendanceRepo.save(AttendanceSummary.builder()
//                                 .organisation(org)
//                                 .employee(emp)
//                                 .attendanceDate(date)
//                                 .status(AttendanceStatus.ON_LEAVE)
//                                 .source(AttendanceSource.LEAVE_SYSTEM)
//                                 .remarks("Auto simulated leave")
//                                 .build());
//                     } else if (roll < 15) { // ~7% absent
//                         attendanceRepo.save(AttendanceSummary.builder()
//                                 .organisation(org)
//                                 .employee(emp)
//                                 .attendanceDate(date)
//                                 .status(AttendanceStatus.ABSENT)
//                                 .source(AttendanceSource.AUTO_SYSTEM)
//                                 .remarks("Auto simulated absence")
//                                 .build());
//                     } else { // ~85% present with punch
//                         LocalDateTime punchIn = date.atTime(9 + random.nextInt(2), random.nextInt(59));
//                         LocalDateTime punchOut = date.atTime(17 + random.nextInt(2), random.nextInt(59));
//                         double hours = Duration.between(punchIn, punchOut).toHours() + random.nextDouble();

//                         PunchInOut punch = punchRepo.save(PunchInOut.builder()
//                                 .organisation(org)
//                                 .employee(emp)
//                                 .punchIn(punchIn)
//                                 .punchOut(punchOut)
//                                 .workHours(hours)
//                                 .punchSource(Source.BIOMETRIC)
//                                 .isAutoPunchGenerated(false)
//                                 .location("HQ Office")
//                                 .build());

//                         attendanceRepo.save(AttendanceSummary.builder()
//                                 .organisation(org)
//                                 .employee(emp)
//                                 .attendanceDate(date)
//                                 .status(AttendanceStatus.PRESENT)
//                                 .punchRecord(punch)
//                                 .effectivePunchIn(punchIn)
//                                 .effectivePunchOut(punchOut)
//                                 .workHours(hours)
//                                 .overtimeHours(hours > 9 ? hours - 9 : 0)
//                                 .isLate(punchIn.getHour() > 9)
//                                 .isEarlyOut(punchOut.getHour() < 17)
//                                 .source(AttendanceSource.PUNCH_SYSTEM)
//                                 .remarks("Simulated attendance record")
//                                 .build());
//                     }

//                     created++;
//                 }
//                 if (created > 0)
//                     log.info("   👤 Employee {} ({}) → {} attendance days generated", emp.getId(), emp.getFirstName(), created);
//             }
//         }

//         log.info("✅ Full attendance re-seed complete for last {} days", DAYS_BACK);
//     }
// }
