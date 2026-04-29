-- V2__Create_notification_tables.sql
-- MySQL 8 / Flyway migration
-- Creates notification tables + seeds master events/templates
-- Uses CURRENT_TIMESTAMP (no hardcoded ids/dates)

-- =====================================================
-- 1. ORG EMAIL CONFIG
-- =====================================================
CREATE TABLE IF NOT EXISTS tbl_org_email_config (
    org_id BIGINT NOT NULL PRIMARY KEY,
    smtp_host VARCHAR(255),
    smtp_port INT,
    smtp_username VARCHAR(255),
    smtp_password TEXT,
    use_tls TINYINT(1) DEFAULT 1,
    use_ssl TINYINT(1) DEFAULT 0,
    from_email VARCHAR(255),
    from_name VARCHAR(255),
    is_active TINYINT(1) DEFAULT 1,
    daily_limit INT DEFAULT 100,
    hourly_limit INT DEFAULT 20,
    sent_today INT DEFAULT 0,
    sent_this_hour INT DEFAULT 0,
    last_reset_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_org_email_config_org
        FOREIGN KEY (org_id)
        REFERENCES tbl_organisation(id)
        ON DELETE CASCADE
);

-- =====================================================
-- 2. MASTER EVENTS
-- =====================================================
CREATE TABLE IF NOT EXISTS tbl_notification_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    event_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- 3. TEMPLATES
-- =====================================================
CREATE TABLE IF NOT EXISTS tbl_notification_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_code VARCHAR(255) NOT NULL,
    target_role VARCHAR(50) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body LONGTEXT NOT NULL,
    is_active TINYINT(1) DEFAULT 1,

    UNIQUE KEY uk_event_role (event_code, target_role)
);

-- =====================================================
-- 4. ORG EVENT PREFERENCES
-- =====================================================
CREATE TABLE IF NOT EXISTS tbl_notification_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    email_enabled TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_org_event (org_id, event_id),

    CONSTRAINT fk_pref_org
        FOREIGN KEY (org_id)
        REFERENCES tbl_organisation(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pref_event
        FOREIGN KEY (event_id)
        REFERENCES tbl_notification_events(id)
        ON DELETE CASCADE
);

-- =====================================================
-- 5. SEED EVENTS
-- =====================================================
INSERT INTO tbl_notification_events (module, event_code, description, is_active)
VALUES
('EMPLOYEE','EMPLOYEE_CREATED','Triggered when a new employee record is created',1),
('EMPLOYEE','EMPLOYEE_UPDATED','Triggered when employee details are updated',0),
('EMPLOYEE','EMPLOYEE_TERMINATED','Triggered when employee is terminated/deactivated',1),


('ATTENDANCE','ATTENDANCE_REGULARIZED','Triggered when attendance is regularized',1),

('LEAVE','LEAVE_APPLIED','Triggered when employee submits leave request',1),
('LEAVE','LEAVE_APPROVED','Triggered when leave request is approved',1),
('LEAVE','LEAVE_REJECTED','Triggered when leave request is rejected',1),

('PRODUCTIVITY','PROJECT_MEMBER_ADDED','Notification sent when employee is added to a project',1),
('PRODUCTIVITY','TICKET_ASSIGNED','Notification sent when ticket is assigned',1),
('PRODUCTIVITY','TICKET_REOPENED','Notification sent when closed ticket is reopened',1),
('PRODUCTIVITY','TASK_OVERDUE','Notification sent when task is overdue',0),
('PRODUCTIVITY','TASK_REMINDER','Notification sent when task reminder time is reached',0)

ON DUPLICATE KEY UPDATE
description = VALUES(description),
module = VALUES(module),
is_active = VALUES(is_active);

-- =====================================================
-- 6. SEED TEMPLATES
-- =====================================================

INSERT INTO tbl_notification_templates
(event_code, target_role, subject, body, is_active)
VALUES

(
'EMPLOYEE_CREATED',
'ADMIN',
'New Employee Added - [[${employeeName}]]',
'<p>Dear [[${recipientName}]],</p>
<p>A new employee <b>[[${employeeName}]]</b> has been successfully added.</p>
<p><b>Department:</b> [[${department}]]</p>
<p><b>Date of Joining:</b> [[${dateOfJoining}]]</p>
<p>Regards,<br/>HRMS System</p>',
1
),

(
'EMPLOYEE_TERMINATED',
'ADMIN',
'Employee Terminated - [[${employeeName}]]',
'<p>Dear [[${recipientName}]],</p>
<p><b>[[${employeeName}]]</b> has been marked as terminated.</p>
<p><b>Last Working Date:</b> [[${dateOfExit}]]</p>
<p>Regards,<br/>HRMS System</p>',
1
),

(
'LEAVE_APPLIED',
'MANAGER',
'Leave Request Submitted - [[${employeeName}]]',
'<p>Dear [[${recipientName}]],</p>
<p><b>[[${employeeName}]]</b> has applied for leave.</p>
<p><b>From:</b> [[${startDate}]]</p>
<p><b>To:</b> [[${endDate}]]</p>
<p><b>Days:</b> [[${leaveDays}]]</p>
<p><b>Reason:</b> [[${reason}]]</p>',
1
),

(
'LEAVE_APPROVED',
'EMPLOYEE',
'Leave Approved',
'<p>Dear [[${employeeName}]],</p>
<p>Your leave request has been <b style=\"color:green;\">approved</b>.</p>
<p><b>Approved By:</b> [[${approver}]]</p>',
1
),

(
'LEAVE_REJECTED',
'EMPLOYEE',
'Leave Rejected',
'<p>Dear [[${employeeName}]],</p>
<p>Your leave request has been <b style=\"color:red;\">rejected</b>.</p>
<p><b>Reviewed By:</b> [[${approver}]]</p>
<p><b>Remarks:</b> [[${remarks}]]</p>',
1
),

(
'ATTENDANCE_REGULARIZED',
'EMPLOYEE',
'Attendance Regularized',
'<p>Dear [[${employeeName}]],</p>
<p>Your attendance has been regularized.</p>
<p><b>Date:</b> [[${date}]]</p>
<p><b>Work Hours:</b> [[${workHours}]]</p>',
1
),

(
'PROJECT_MEMBER_ADDED',
'EMPLOYEE',
'You have been added to project [[${projectName}]]',
'<p>Dear [[${recipientName}]],</p>
<p>You have been added to <b>[[${projectName}]]</b>.</p>
<p><b>Role:</b> [[${projectRole}]]</p>
<p><b>Added By:</b> [[${addedBy}]]</p>',
1
),

(
'TICKET_ASSIGNED',
'EMPLOYEE',
'Ticket Assigned: [[${ticketTitle}]]',
'<p>Dear [[${recipientName}]],</p>
<p>A ticket has been assigned to you.</p>
<p><b>Title:</b> [[${ticketTitle}]]</p>
<p><b>Assigned By:</b> [[${assignedBy}]]</p>',
1
),

(
'TICKET_REOPENED',
'EMPLOYEE',
'Ticket Reopened: [[${ticketTitle}]]',
'<p>Dear [[${recipientName}]],</p>
<p>The following ticket has been reopened:</p>
<p><b>Title:</b> [[${ticketTitle}]]</p>
<p><b>Reopened By:</b> [[${reopenedBy}]]</p>',
1
),

(
'TASK_REMINDER',
'EMPLOYEE',
'Reminder: [[${taskTitle}]]',
'<p>Dear [[${recipientName}]],</p>
<p>This is a reminder for your task.</p>
<p><b>Task:</b> [[${taskTitle}]]</p>
<p><b>Reminder Time:</b> [[${reminderAt}]]</p>',
1
),

(
'TASK_OVERDUE',
'EMPLOYEE',
'Task Overdue: [[${taskTitle}]]',
'<p>Dear [[${recipientName}]],</p>
<p>Your task is overdue.</p>
<p><b>Task:</b> [[${taskTitle}]]</p>
<p><b>Due Date:</b> [[${dueDate}]]</p>
<p><b>Status:</b> [[${status}]]</p>',
1
)

ON DUPLICATE KEY UPDATE
subject = VALUES(subject),
body = VALUES(body),
is_active = VALUES(is_active);