// package com.sellspark.SellsHRMS.service;

// import com.sellspark.SellsHRMS.entity.Leave;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// /**
//  * Service for sending leave-related notifications
//  * TODO: Integrate with email/SMS service
//  */
// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class LeaveNotificationService {

//     /**
//      * Notify employee when leave is applied
//      */
//     public void notifyLeaveApplied(Leave leave) {
//         log.info("Notification: Leave applied by employee {} for dates {} to {}",
//                 leave.getEmployee().getEmployeeCode(),
//                 leave.getStartDate(),
//                 leave.getEndDate());

//         // TODO: Send email to employee
//         // TODO: Send notification to reporting manager
//         // TODO: Send SMS if configured

//         String message = String.format(
//                 "Dear %s, your leave application for %d days (%s to %s) has been submitted and is pending approval.",
//                 leave.getEmployee().getFirstName(),
//                 leave.getTotalDays(),
//                 leave.getStartDate(),
//                 leave.getEndDate()
//         );

//         log.debug("Leave application notification: {}", message);
//     }

//     /**
//      * Notify employee when leave is approved
//      */
//     public void notifyLeaveApproved(Leave leave) {
//         log.info("Notification: Leave approved for employee {} by {}",
//                 leave.getEmployee().getEmployeeCode(),
//                 leave.getApprovedBy() != null ? leave.getApprovedBy().getEmployeeCode() : "System");

//         // TODO: Send email to employee
//         // TODO: Send SMS if configured

//         String message = String.format(
//                 "Dear %s, your leave application for %d days (%s to %s) has been APPROVED by %s.",
//                 leave.getEmployee().getFirstName(),
//                 leave.getTotalDays(),
//                 leave.getStartDate(),
//                 leave.getEndDate(),
//                 leave.getApprovedBy() != null ? 
//                     leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName() : 
//                     "System"
//         );

//         log.debug("Leave approval notification: {}", message);
//     }

//     /**
//      * Notify employee when leave is rejected
//      */
//     public void notifyLeaveRejected(Leave leave) {
//         log.info("Notification: Leave rejected for employee {} by {}",
//                 leave.getEmployee().getEmployeeCode(),
//                 leave.getApprovedBy() != null ? leave.getApprovedBy().getEmployeeCode() : "System");

//         // TODO: Send email to employee
//         // TODO: Send SMS if configured

//         String message = String.format(
//                 "Dear %s, your leave application for %d days (%s to %s) has been REJECTED by %s.",
//                 leave.getEmployee().getFirstName(),
//                 leave.getTotalDays(),
//                 leave.getStartDate(),
//                 leave.getEndDate(),
//                 leave.getApprovedBy() != null ? 
//                     leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName() : 
//                     "System"
//         );

//         log.debug("Leave rejection notification: {}", message);
//     }

//     /**
//      * Notify manager about pending leave approvals
//      */
//     public void notifyManagerPendingLeaves(Long managerId, int pendingCount) {
//         log.info("Notification: Manager {} has {} pending leave approvals", managerId, pendingCount);

//         // TODO: Send email to manager
//         // TODO: Send in-app notification

//         String message = String.format(
//                 "You have %d pending leave approval(s) waiting for your action.",
//                 pendingCount
//         );

//         log.debug("Manager pending leaves notification: {}", message);
//     }

//     /**
//      * Reminder notification for upcoming leaves
//      */
//     public void notifyUpcomingLeave(Leave leave) {
//         log.info("Notification: Upcoming leave reminder for employee {}",
//                 leave.getEmployee().getEmployeeCode());

//         // TODO: Send email reminder
//         // TODO: Send SMS if configured

//         String message = String.format(
//                 "Reminder: Your approved leave starts on %s for %d days.",
//                 leave.getStartDate(),
//                 leave.getTotalDays()
//         );

//         log.debug("Upcoming leave reminder: {}", message);
//     }
// }