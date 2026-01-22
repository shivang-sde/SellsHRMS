// package com.sellspark.SellsHRMS.service;

// import com.sellspark.SellsHRMS.entity.Leave;
// import com.sellspark.SellsHRMS.entity.LeaveType;
// import com.sellspark.SellsHRMS.repository.LeaveRepository;
// import com.sellspark.SellsHRMS.repository.LeaveTypeRepository;
// import lombok.Data;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;
// import java.time.Month;
// import java.util.*;
// import java.util.stream.Collectors;

// /**
//  * Service for generating leave statistics and reports
//  */
// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class LeaveStatisticsService {

//     private final LeaveRepository leaveRepo;
//     private final LeaveTypeRepository leaveTypeRepo;

//     /**
//      * Get organisation-wide leave statistics
//      */
//     public OrgLeaveStats getOrgLeaveStats(Long orgId, int year) {
//         List<Leave> leaves = leaveRepo.findByOrganisationId(orgId).stream()
//                 .filter(l -> l.getStartDate().getYear() == year)
//                 .toList();

//         OrgLeaveStats stats = new OrgLeaveStats();
//         stats.setYear(year);
//         stats.setTotalLeaveApplications(leaves.size());

//         // Count by status
//         long pending = leaves.stream()
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.PENDING)
//                 .count();
//         long approved = leaves.stream()
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .count();
//         long rejected = leaves.stream()
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.REJECTED)
//                 .count();

//         stats.setPendingLeaves((int) pending);
//         stats.setApprovedLeaves((int) approved);
//         stats.setRejectedLeaves((int) rejected);

//         // Total leave days
//         int totalDays = leaves.stream()
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .mapToInt(Leave::getTotalDays)
//                 .sum();
//         stats.setTotalLeaveDays(totalDays);

//         // Average leave days per employee (who took leave)
//         long uniqueEmployees = leaves.stream()
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .map(l -> l.getEmployee().getId())
//                 .distinct()
//                 .count();
        
//         if (uniqueEmployees > 0) {
//             stats.setAverageLeaveDaysPerEmployee((double) totalDays / uniqueEmployees);
//         }

//         return stats;
//     }

//     /**
//      * Get leave statistics by type
//      */
//     public List<LeaveTypeStats> getLeaveStatsByType(Long orgId, int year) {
//         List<LeaveType> leaveTypes = leaveTypeRepo.findByOrganisationId(orgId);
//         List<Leave> leaves = leaveRepo.findByOrganisationId(orgId).stream()
//                 .filter(l -> l.getStartDate().getYear() == year)
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .toList();

//         return leaveTypes.stream()
//                 .map(type -> {
//                     List<Leave> typeLeaves = leaves.stream()
//                             .filter(l -> l.getLeaveType().getId().equals(type.getId()))
//                             .toList();

//                     LeaveTypeStats stats = new LeaveTypeStats();
//                     stats.setLeaveTypeName(type.getName());
//                     stats.setTotalApplications(typeLeaves.size());
//                     stats.setTotalDays(typeLeaves.stream()
//                             .mapToInt(Leave::getTotalDays)
//                             .sum());

//                     return stats;
//                 })
//                 .collect(Collectors.toList());
//     }

//     /**
//      * Get monthly leave trends
//      */
//     public Map<String, Integer> getMonthlyLeaveTrend(Long orgId, int year) {
//         List<Leave> leaves = leaveRepo.findByOrganisationId(orgId).stream()
//                 .filter(l -> l.getStartDate().getYear() == year)
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .toList();

//         Map<String, Integer> monthlyStats = new LinkedHashMap<>();
        
//         // Initialize all months with 0
//         for (Month month : Month.values()) {
//             monthlyStats.put(month.name(), 0);
//         }

//         // Count leaves by start month
//         leaves.forEach(leave -> {
//             String month = leave.getStartDate().getMonth().name();
//             monthlyStats.put(month, monthlyStats.get(month) + 1);
//         });

//         return monthlyStats;
//     }

//     /**
//      * Get top leave takers
//      */
//     public List<TopLeaveTaker> getTopLeaveTakers(Long orgId, int year, int limit) {
//         List<Leave> leaves = leaveRepo.findByOrganisationId(orgId).stream()
//                 .filter(l -> l.getStartDate().getYear() == year)
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .toList();

//         // Group by employee
//         Map<Long, List<Leave>> leavesByEmployee = leaves.stream()
//                 .collect(Collectors.groupingBy(l -> l.getEmployee().getId()));

//         return leavesByEmployee.entrySet().stream()
//                 .map(entry -> {
//                     Leave firstLeave = entry.getValue().get(0);
//                     int totalDays = entry.getValue().stream()
//                             .mapToInt(Leave::getTotalDays)
//                             .sum();

//                     TopLeaveTaker taker = new TopLeaveTaker();
//                     taker.setEmployeeId(firstLeave.getEmployee().getId());
//                     taker.setEmployeeName(firstLeave.getEmployee().getFirstName() + " " + 
//                                         firstLeave.getEmployee().getLastName());
//                     taker.setEmployeeCode(firstLeave.getEmployee().getEmployeeCode());
//                     taker.setTotalLeaveDays(totalDays);
//                     taker.setLeaveCount(entry.getValue().size());

//                     return taker;
//                 })
//                 .sorted((a, b) -> Integer.compare(b.getTotalLeaveDays(), a.getTotalLeaveDays()))
//                 .limit(limit)
//                 .collect(Collectors.toList());
//     }

//     /**
//      * Get leave pattern analysis
//      */
//     public LeavePatternAnalysis getLeavePatternAnalysis(Long orgId, int year) {
//         List<Leave> leaves = leaveRepo.findByOrganisationId(orgId).stream()
//                 .filter(l -> l.getStartDate().getYear() == year)
//                 .filter(l -> l.getLeaveStatus() == Leave.LeaveStatus.APPROVE)
//                 .toList();

//         LeavePatternAnalysis analysis = new LeavePatternAnalysis();

//         // Count leaves by day of week (start day)
//         Map<String, Integer> dayOfWeekPattern = leaves.stream()
//                 .collect(Collectors.groupingBy(
//                         l -> l.getStartDate().getDayOfWeek().name(),
//                         Collectors.summingInt(l -> 1)
//                 ));
//         analysis.setDayOfWeekPattern(dayOfWeekPattern);

//         // Count short vs long leaves
//         long shortLeaves = leaves.stream()
//                 .filter(l -> l.getTotalDays() <= 3)
//                 .count();
//         long longLeaves = leaves.stream()
//                 .filter(l -> l.getTotalDays() > 3)
//                 .count();

//         analysis.setShortLeaves((int) shortLeaves);
//         analysis.setLongLeaves((int) longLeaves);

//         // Average approval time (in days)
//         // TODO: Need to track application date and approval date
        
//         return analysis;
//     }

//     // ========== DTO Classes ==========

//     @Data
//     public static class OrgLeaveStats {
//         private int year;
//         private int totalLeaveApplications;
//         private int pendingLeaves;
//         private int approvedLeaves;
//         private int rejectedLeaves;
//         private int totalLeaveDays;
//         private double averageLeaveDaysPerEmployee;
//     }

//     @Data
//     public static class LeaveTypeStats {
//         private String leaveTypeName;
//         private int totalApplications;
//         private int totalDays;
//     }

//     @Data
//     public static class TopLeaveTaker {
//         private Long employeeId;
//         private String employeeName;
//         private String employeeCode;
//         private int totalLeaveDays;
//         private int leaveCount;
//     }

//     @Data
//     public static class LeavePatternAnalysis {
//         private Map<String, Integer> dayOfWeekPattern;
//         private int shortLeaves;  // <= 3 days
//         private int longLeaves;   // > 3 days
//         private double averageApprovalTime;
//     }
// }