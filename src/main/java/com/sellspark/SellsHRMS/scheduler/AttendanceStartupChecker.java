// package com.sellspark.SellsHRMS.scheduler;

// import com.sellspark.SellsHRMS.entity.Employee;
// import com.sellspark.SellsHRMS.repository.AttendanceSummaryRepository;
// import com.sellspark.SellsHRMS.repository.OrganisationRepository;
// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// import java.time.LocalDate;
// import java.time.ZonedDateTime;
// import java.util.Collection;
// import java.util.TimeZone;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class AttendanceStartupChecker {

// private final AttendanceScheduler scheduler;
// private final AttendanceSummaryRepository summaryRepo;
// private final OrganisationRepository organisationRepo;
// private final com.sellspark.SellsHRMS.repository.EmployeeRepository
// employeeRepository;
// private final com.sellspark.SellsHRMS.service.LeaveService leaveService;

// @PostConstruct
// public void ensureDailyAttendanceExists() {
// LocalDate today = LocalDate.now();
// log.info("Checking attendance initialization for {} ...", today);
// log.info("time zone...", TimeZone.getDefault());
// log.info("zone date time now", ZonedDateTime.now());
// System.out.println(TimeZone.getDefault());
// System.out.println(ZonedDateTime.now());

// organisationRepo.findAll().stream()
// .filter(org -> Boolean.TRUE.equals(org.getIsActive()))
// .forEach(org -> {
// long count = summaryRepo.countByOrganisationIdAndAttendanceDate(org.getId(),
// today);

// if (count == 0) {
// log.warn("⚠️ No attendance summaries found for org: {} (ID: {}). Running
// preMarkDailyAttendance...",
// org.getName(), org.getId());
// try {
// scheduler.preMarkDailyAttendance();
// log.info("✅ Attendance pre-mark completed for org: {}", org.getName());
// } catch (Exception e) {
// log.error("❌ Failed to pre-mark attendance for org {}: {}", org.getName(),
// e.getMessage());
// }
// } else {
// log.info("✅ Attendance already exists for org: {} ({} records)",
// org.getName(), count);
// }
// });

// log.info("AttendanceStartupChecker completed.");
// }

// // @PostConstruct
// // public void seedLeavesForExistingEmployees() {
// // log.info("Starting leave balance initialization for existing
// employees...");
// // Iterable<Employee> employees = employeeRepository.findAll();
// // log.info("employees fetched: {}", ((Collection<?>) employees).size());
// // for (Employee employee : employees) {
// // try {
// // String leaveYear =
// leaveService.getCurrentLeaveYear(employee.getOrganisation().getId());
// // leaveService.initializeLeaveBalancesForEmployee(employee.getId(),
// employee.getOrganisation().getId(), leaveYear);
// // log.info("Initialized leave balances for employee ID: " +
// employee.getId());
// // } catch (Exception e) {
// // log.error("Failed to initialize leave balances for employee ID: " +
// employee.getId(), e);
// // }
// // }
// // }
// }
