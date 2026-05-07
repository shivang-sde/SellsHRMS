-- =====================================================
-- URL Monitor Module - Flyway Migration Script
-- Version: V3__Url_monitor_tables_and_data.sql (or your version)
-- =====================================================



-- =====================================================
-- 1. MONITOR_URLS - Core table for monitored endpoints
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_urls (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    organisation_id BIGINT NOT NULL COMMENT 'Foreign key to tbl_organisation',
    name VARCHAR(200) NOT NULL COMMENT 'Human readable name for the URL',
    url VARCHAR(1000) NOT NULL COMMENT 'The actual URL to monitor',
    method ENUM('GET', 'POST', 'HEAD') NOT NULL DEFAULT 'GET' COMMENT 'HTTP method to use',
    check_interval INT NOT NULL DEFAULT 300 COMMENT 'Check interval in seconds',
    timeout INT NOT NULL DEFAULT 30 COMMENT 'Request timeout in seconds',
    expected_status VARCHAR(20) DEFAULT '2xx' COMMENT 'Expected HTTP status code pattern (2xx, 200, 301, etc.)',
    headers JSON DEFAULT NULL COMMENT 'Custom HTTP headers as JSON object',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Whether monitoring is active',
    current_status ENUM('up', 'down', 'pending') NOT NULL DEFAULT 'pending' COMMENT 'Current status of the URL',
    last_checked_at DATETIME DEFAULT NULL COMMENT 'Timestamp of last check',
    last_response_time INT DEFAULT NULL COMMENT 'Last response time in milliseconds',
    last_status_code INT DEFAULT NULL COMMENT 'Last HTTP status code received',
    last_error TEXT DEFAULT NULL COMMENT 'Last error message if any',
    uptime_percentage DECIMAL(5,2) DEFAULT 100.00 COMMENT 'Uptime percentage for last 24 hours',
    consecutive_failures INT NOT NULL DEFAULT 0 COMMENT 'Number of consecutive failures',
    failure_threshold INT NOT NULL DEFAULT 3 COMMENT 'Failures needed before marking as down',
    created_by BIGINT NOT NULL COMMENT 'User who created this URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    
    INDEX idx_organisation_id (organisation_id),
    INDEX idx_is_active (is_active),
    INDEX idx_current_status (current_status),
    INDEX idx_created_by (created_by),
    INDEX idx_last_checked_at (last_checked_at),
    INDEX idx_organisation_name (organisation_id, name),
    INDEX idx_active_next_check (is_active, last_checked_at, check_interval),
    
    CONSTRAINT fk_monitor_urls_organisation FOREIGN KEY (organisation_id) 
        REFERENCES tbl_organisation(id) ON DELETE CASCADE,
    CONSTRAINT fk_monitor_urls_created_by FOREIGN KEY (created_by) 
        REFERENCES tbl_user(id),
    CONSTRAINT uk_monitor_urls_org_name UNIQUE (organisation_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 2. MONITOR_CHECKS - Individual check results
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_checks (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    url_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to monitor_urls',
    status_code INT DEFAULT NULL COMMENT 'HTTP status code received',
    response_time INT DEFAULT NULL COMMENT 'Response time in milliseconds',
    is_up BOOLEAN NOT NULL COMMENT 'Whether the check was successful',
    error TEXT DEFAULT NULL COMMENT 'Error message if check failed',
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the check was performed',
    
    INDEX idx_url_id (url_id),
    INDEX idx_checked_at (checked_at),
    INDEX idx_url_checked (url_id, checked_at),
    INDEX idx_is_up_checked (is_up, checked_at),
    INDEX idx_response_time_checked (response_time, checked_at),
    INDEX idx_url_status (url_id, is_up, checked_at),
    
    CONSTRAINT fk_monitor_checks_url FOREIGN KEY (url_id) 
        REFERENCES monitor_urls(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 3. MONITOR_INCIDENTS - Downtime tracking
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_incidents (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    url_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to monitor_urls',
    started_at DATETIME NOT NULL COMMENT 'When the incident started',
    ended_at DATETIME DEFAULT NULL COMMENT 'When the incident ended (null if active)',
    duration_seconds INT DEFAULT NULL COMMENT 'Total duration in seconds',
    cause VARCHAR(500) DEFAULT NULL COMMENT 'Error message or status code causing the incident',
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether incident is resolved',
    notification_sent BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether down notification was sent',
    recovery_notification_sent BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Whether recovery notification was sent',
    
    INDEX idx_url_id (url_id),
    INDEX idx_started_at (started_at),
    INDEX idx_is_resolved (is_resolved),
    INDEX idx_url_resolved (url_id, is_resolved),
    INDEX idx_resolved_duration (is_resolved, duration_seconds),
    INDEX idx_started_ended (started_at, ended_at),
    
    CONSTRAINT fk_monitor_incidents_url FOREIGN KEY (url_id) 
        REFERENCES monitor_urls(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 4. MONITOR_GROUPS - Notification groups
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_groups (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    organisation_id BIGINT NOT NULL COMMENT 'Foreign key to tbl_organisation',
    name VARCHAR(200) NOT NULL COMMENT 'Group name',
    description TEXT DEFAULT NULL COMMENT 'Group description',
    created_by BIGINT NOT NULL COMMENT 'User who created this group',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    
    INDEX idx_organisation_id (organisation_id),
    INDEX idx_created_by (created_by),
    INDEX idx_organisation_name (organisation_id, name),
    
    CONSTRAINT fk_monitor_groups_organisation FOREIGN KEY (organisation_id) 
        REFERENCES tbl_organisation(id) ON DELETE CASCADE,
    CONSTRAINT fk_monitor_groups_created_by FOREIGN KEY (created_by) 
        REFERENCES tbl_user(id),
    CONSTRAINT uk_monitor_groups_org_name UNIQUE (organisation_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 5. MONITOR_GROUP_MEMBERS - Group members (recipients)
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_group_members (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    group_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to monitor_groups',
    user_id BIGINT NOT NULL COMMENT 'Foreign key to tbl_user - member who receives notifications',
    added_by BIGINT NOT NULL COMMENT 'User who added this member',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When member was added',
    
    INDEX idx_group_id (group_id),
    INDEX idx_user_id (user_id),
    INDEX idx_added_by (added_by),
    
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) 
        REFERENCES monitor_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) 
        REFERENCES tbl_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_members_added_by FOREIGN KEY (added_by) 
        REFERENCES tbl_user(id),
    CONSTRAINT uk_group_members_group_user UNIQUE (group_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 6. MONITOR_GROUP_URLS - Links groups to URLs
-- =====================================================
CREATE TABLE IF NOT EXISTS monitor_group_urls (
    id VARCHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    group_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to monitor_groups',
    url_id VARCHAR(36) NOT NULL COMMENT 'Foreign key to monitor_urls',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When URL was added to group',
    
    INDEX idx_group_id (group_id),
    INDEX idx_url_id (url_id),
    
    CONSTRAINT fk_group_urls_group FOREIGN KEY (group_id) 
        REFERENCES monitor_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_urls_url FOREIGN KEY (url_id) 
        REFERENCES monitor_urls(id) ON DELETE CASCADE,
    CONSTRAINT uk_group_urls_group_url UNIQUE (group_id, url_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================
-- 7. INSERT NOTIFICATION EVENTS FOR URL MONITORING
-- =====================================================
-- Note: Using module = 'URL_MONITOR' and event_code as per your schema

INSERT INTO tbl_notification_events (module, event_code, description, is_active, created_at)
SELECT * FROM (SELECT 'URL_MONITOR' as module, 'MONITOR_URL_DOWN' as event_code, 
    'Sent when a monitored URL goes down after failure threshold' as description,
    1 as is_active, NOW() as created_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_events WHERE event_code = 'MONITOR_URL_DOWN') LIMIT 1;

INSERT INTO tbl_notification_events (module, event_code, description, is_active, created_at)
SELECT * FROM (SELECT 'URL_MONITOR' as module, 'MONITOR_URL_RECOVERED' as event_code,
    'Sent when a down URL comes back up' as description,
    1 as is_active, NOW() as created_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_events WHERE event_code = 'MONITOR_URL_RECOVERED') LIMIT 1;


-- =====================================================
-- 8. INSERT EMAIL TEMPLATES FOR URL MONITORING
-- =====================================================

-- Template for URL DOWN - ORG_ADMIN
INSERT INTO tbl_notification_templates (event_code, target_role, subject, body, is_active)
SELECT * FROM (SELECT 'MONITOR_URL_DOWN' as event_code, 'ADMIN' as target_role,
    '🔴 {{organisationName}}: {{urlName}} is DOWN' as subject,
    '<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
<div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
    <div style="background: #dc2626; padding: 20px; text-align: center;">
        <h1 style="color: white; margin: 0;">🔴 URL DOWN ALERT</h1>
        <p style="color: #fecaca; margin: 5px 0 0;">{{organisationName}}</p>
    </div>
    <div style="padding: 20px;">
        <p style="font-size: 16px; color: #333;">Dear <strong>{{recipientName}}</strong>,</p>
        <p style="font-size: 16px; color: #333;">A monitored URL for your organisation is <strong style="color: #dc2626;">DOWN</strong>:</p>
        
        <div style="background: #fef2f2; border-left: 4px solid #dc2626; padding: 15px; margin: 15px 0; border-radius: 4px;">
            <p style="margin: 5px 0;"><strong>📛 URL Name:</strong> {{urlName}}</p>
            <p style="margin: 5px 0;"><strong>🔗 URL Address:</strong> <a href="{{urlAddress}}" style="color: #dc2626;">{{urlAddress}}</a></p>
            <p style="margin: 5px 0;"><strong>⏰ Down Since:</strong> {{downTime}}</p>
            {{#if cause}}
            <p style="margin: 5px 0;"><strong>❌ Error:</strong> {{cause}}</p>
            {{/if}}
        </div>
        
        <p style="font-size: 14px; color: #666;">This alert was triggered after {{failureThreshold}} consecutive failures.</p>
        <p style="font-size: 14px; color: #666;">Please investigate the issue as soon as possible.</p>
        
        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
        
        <p style="font-size: 12px; color: #999; text-align: center;">
            <a href="{{dashboardUrl}}" style="color: #3b82f6;">View in Dashboard</a><br>
            This is an automated alert from SellsHRMS.
        </p>
    </div>
</div>
</body>
</html>' as body,
    1 as is_active) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_templates WHERE event_code = 'MONITOR_URL_DOWN' AND target_role = 'ORG_ADMIN') LIMIT 1;

-- Template for URL RECOVERED - ORG_ADMIN
INSERT INTO tbl_notification_templates (event_code, target_role, subject, body, is_active)
SELECT * FROM (SELECT 'MONITOR_URL_RECOVERED' as event_code, 'ADMIN' as target_role,
    '🟢 {{organisationName}}: {{urlName}} is back UP' as subject,
    '<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
<div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
    <div style="background: #10b981; padding: 20px; text-align: center;">
        <h1 style="color: white; margin: 0;">🟢 URL RECOVERED</h1>
        <p style="color: #a7f3d0; margin: 5px 0 0;">{{organisationName}}</p>
    </div>
    <div style="padding: 20px;">
        <p style="font-size: 16px; color: #333;">Dear <strong>{{recipientName}}</strong>,</p>
        <p style="font-size: 16px; color: #333;">A previously down URL for your organisation has <strong style="color: #10b981;">recovered</strong>:</p>
        
        <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 15px; margin: 15px 0; border-radius: 4px;">
            <p style="margin: 5px 0;"><strong>📛 URL Name:</strong> {{urlName}}</p>
            <p style="margin: 5px 0;"><strong>🔗 URL Address:</strong> <a href="{{urlAddress}}" style="color: #10b981;">{{urlAddress}}</a></p>
            <p style="margin: 5px 0;"><strong>✅ Recovered At:</strong> {{recoveryTime}}</p>
            <p style="margin: 5px 0;"><strong>⏱️ Downtime Duration:</strong> {{downtimeDuration}}</p>
        </div>
        
        <p style="font-size: 14px; color: #666;">The service is now operational.</p>
        
        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
        
        <p style="font-size: 12px; color: #999; text-align: center;">
            <a href="{{dashboardUrl}}" style="color: #3b82f6;">View Details</a><br>
            This is an automated recovery notification from SellsHRMS.
        </p>
    </div>
</div>
</body>
</html>' as body,
    1 as is_active) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_templates WHERE event_code = 'MONITOR_URL_RECOVERED' AND target_role = 'ORG_ADMIN') LIMIT 1;

-- Template for URL DOWN - SUPER_ADMIN
INSERT INTO tbl_notification_templates (event_code, target_role, subject, body, is_active)
SELECT * FROM (SELECT 'MONITOR_URL_DOWN' as event_code, 'SUPERADMIN' as target_role,
    '🔴 ALERT: {{urlName}} is DOWN ({{organisationName}})' as subject,
    '<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
<div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
    <div style="background: #dc2626; padding: 20px; text-align: center;">
        <h1 style="color: white; margin: 0;">🔴 URL DOWN ALERT</h1>
    </div>
    <div style="padding: 20px;">
        <p style="font-size: 16px; color: #333;">Dear <strong>{{recipientName}}</strong>,</p>
        <p style="font-size: 16px; color: #333;">A monitored URL has been detected as <strong style="color: #dc2626;">DOWN</strong>:</p>
        
        <div style="background: #fef2f2; border-left: 4px solid #dc2626; padding: 15px; margin: 15px 0; border-radius: 4px;">
            <p style="margin: 5px 0;"><strong>🏢 Organisation:</strong> {{organisationName}}</p>
            <p style="margin: 5px 0;"><strong>📛 URL Name:</strong> {{urlName}}</p>
            <p style="margin: 5px 0;"><strong>🔗 URL Address:</strong> <a href="{{urlAddress}}" style="color: #dc2626;">{{urlAddress}}</a></p>
            <p style="margin: 5px 0;"><strong>⏰ Down Since:</strong> {{downTime}}</p>
            {{#if cause}}
            <p style="margin: 5px 0;"><strong>❌ Error:</strong> {{cause}}</p>
            {{/if}}
        </div>
        
        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
        
        <p style="font-size: 12px; color: #999; text-align: center;">
            This is an automated alert from SellsHRMS.
        </p>
    </div>
</div>
</body>
</html>' as body,
    1 as is_active) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_templates WHERE event_code = 'MONITOR_URL_DOWN' AND target_role = 'SUPERADMIN') LIMIT 1;

-- Template for URL RECOVERED - SUPER_ADMIN
INSERT INTO tbl_notification_templates (event_code, target_role, subject, body, is_active)
SELECT * FROM (SELECT 'MONITOR_URL_RECOVERED' as event_code, 'SUPERADMIN' as target_role,
    '🟢 RECOVERED: {{urlName}} is back UP ({{organisationName}})' as subject,
    '<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"></head>
<body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
<div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
    <div style="background: #10b981; padding: 20px; text-align: center;">
        <h1 style="color: white; margin: 0;">🟢 URL RECOVERED</h1>
    </div>
    <div style="padding: 20px;">
        <p style="font-size: 16px; color: #333;">Dear <strong>{{recipientName}}</strong>,</p>
        <p style="font-size: 16px; color: #333;">A previously down URL has recovered:</p>
        
        <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 15px; margin: 15px 0; border-radius: 4px;">
            <p style="margin: 5px 0;"><strong>🏢 Organisation:</strong> {{organisationName}}</p>
            <p style="margin: 5px 0;"><strong>📛 URL Name:</strong> {{urlName}}</p>
            <p style="margin: 5px 0;"><strong>🔗 URL Address:</strong> <a href="{{urlAddress}}" style="color: #10b981;">{{urlAddress}}</a></p>
            <p style="margin: 5px 0;"><strong>✅ Recovered At:</strong> {{recoveryTime}}</p>
            <p style="margin: 5px 0;"><strong>⏱️ Downtime Duration:</strong> {{downtimeDuration}}</p>
        </div>
        
        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
        
        <p style="font-size: 12px; color: #999; text-align: center;">
            This is an automated recovery notification from SellsHRMS.
        </p>
    </div>
</div>
</body>
</html>' as body,
    1 as is_active) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM tbl_notification_templates WHERE event_code = 'MONITOR_URL_RECOVERED' AND target_role = 'SUPERADMIN') LIMIT 1;


-- =====================================================
-- 9. ENABLE NOTIFICATIONS BY DEFAULT FOR EXISTING ORGANISATIONS
-- =====================================================

-- Get event IDs for URL monitor events
SET @down_event_id = (SELECT id FROM tbl_notification_events WHERE event_code = 'MONITOR_URL_DOWN' LIMIT 1);
SET @recovered_event_id = (SELECT id FROM tbl_notification_events WHERE event_code = 'MONITOR_URL_RECOVERED' LIMIT 1);

-- Enable notifications for MONITOR_URL_DOWN
INSERT INTO tbl_notification_preference (org_id, event_id, email_enabled, created_at)
SELECT 
    o.id as org_id,
    @down_event_id as event_id,
    1 as email_enabled,
    NOW() as created_at
FROM tbl_organisation o
WHERE @down_event_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM tbl_notification_preference np 
    WHERE np.org_id = o.id AND np.event_id = @down_event_id
);

-- Enable notifications for MONITOR_URL_RECOVERED
INSERT INTO tbl_notification_preference (org_id, event_id, email_enabled, created_at)
SELECT 
    o.id as org_id,
    @recovered_event_id as event_id,
    1 as email_enabled,
    NOW() as created_at
FROM tbl_organisation o
WHERE @recovered_event_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM tbl_notification_preference np 
    WHERE np.org_id = o.id AND np.event_id = @recovered_event_id
);


-- =====================================================
-- 10. VERIFICATION QUERIES (for debugging)
-- =====================================================

-- Verify tables created
SELECT '=== URL Monitor Tables Created ===' as '';
SELECT table_name FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name LIKE 'monitor_%'
ORDER BY table_name;

-- Verify notification events
SELECT '=== Notification Events ===' as '';
SELECT id, module, event_code, description FROM tbl_notification_events 
WHERE module = 'URL_MONITOR';

-- Verify notification templates
SELECT '=== Notification Templates ===' as '';
SELECT event_code, target_role, LEFT(subject, 60) as subject_preview 
FROM tbl_notification_templates 
WHERE event_code IN ('MONITOR_URL_DOWN', 'MONITOR_URL_RECOVERED');

-- Verify notification preferences
SELECT '=== Default Notification Preferences ===' as '';
SELECT COUNT(DISTINCT org_id) as orgs_with_preferences 
FROM tbl_notification_preference np
WHERE np.event_id IN (@down_event_id, @recovered_event_id);