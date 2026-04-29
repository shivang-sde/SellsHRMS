-- V2__Create_notification_tables.sql
-- MySQL + Flyway + Spring Boot

-- 1. NotificationEvent (no FKs → create first)
CREATE TABLE IF NOT EXISTS tbl_notification_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    event_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_event_code (event_code),
    INDEX idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. OrgEmailConfig (FK to Organisation - assumed to exist)
CREATE TABLE IF NOT EXISTS tbl_org_email_config (
    org_id BIGINT NOT NULL PRIMARY KEY,
    smtp_host VARCHAR(255),
    smtp_port INT,
    smtp_username VARCHAR(255),
    smtp_password TEXT,  -- Encrypted via SmtpPasswordConverter
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
        FOREIGN KEY (org_id) REFERENCES organisation(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. NotificationTemplate
CREATE TABLE IF NOT EXISTS tbl_notification_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_code VARCHAR(255) NOT NULL,
    target_role ENUM('ADMIN','HR','EMPLOYEE','MANAGER','SUPERADMIN','ALL') NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body LONGTEXT NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    org_id BIGINT NOT NULL,  -- Logical FK (no physical constraint if org may be deleted)
    UNIQUE KEY uk_org_event_role (org_id, event_code, target_role),
    INDEX idx_event_code (event_code),
    INDEX idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. NotificationPreference (FKs to Organisation + NotificationEvent)
CREATE TABLE IF NOT EXISTS tbl_notification_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    email_enabled TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_org_event (org_id, event_id),
    INDEX idx_org_id (org_id),
    INDEX idx_event_id (event_id),
    CONSTRAINT fk_pref_org 
        FOREIGN KEY (org_id) REFERENCES organisation(id) ON DELETE CASCADE,
    CONSTRAINT fk_pref_event 
        FOREIGN KEY (event_id) REFERENCES tbl_notification_events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;