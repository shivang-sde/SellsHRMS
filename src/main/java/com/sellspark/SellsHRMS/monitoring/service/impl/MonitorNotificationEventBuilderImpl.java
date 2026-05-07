package com.sellspark.SellsHRMS.monitoring.service.impl;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorIncident;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;
import com.sellspark.SellsHRMS.monitoring.service.MonitorNotificationEventBuilder;
import com.sellspark.SellsHRMS.notification.event.NotificationEventData;
import com.sellspark.SellsHRMS.notification.event.NotificationEventPublisher;
import com.sellspark.SellsHRMS.repository.UserRepository;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorNotificationEventBuilderImpl implements MonitorNotificationEventBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a");
    private final NotificationEventPublisher notificationEventPublisher;
    private final UserRepository userRepository;

    /**
     * Build notification event for URL DOWN alert
     */
    @Override
    public NotificationEventData buildUrlDownEvent(MonitorUrl url, MonitorIncident incident) {
        Organisation org = url.getOrganisation();
        User createdBy = url.getCreatedBy();

        Map<String, Object> variables = new HashMap<>();
        variables.put("urlName", url.getName());
        variables.put("urlAddress", url.getUrl());
        variables.put("downTime", incident.getStartedAt().format(DATE_TIME_FORMATTER));
        variables.put("cause", incident.getCause() != null ? incident.getCause() : "Connection failed");
        variables.put("failureThreshold", url.getFailureThreshold());
        variables.put("uptimePercentage", url.getUptimePercentage());
        variables.put("checkInterval", formatInterval(url.getCheckInterval()));
        variables.put("createdAt", url.getCreatedAt().format(DATE_TIME_FORMATTER));
        variables.put("organisationName", org != null ? org.getName() : "System");
        variables.put("systemName", "SellsHRMS");
        variables.put("dashboardUrl", "/monitor/urls/" + url.getId());

        return NotificationEventData.builder()
                .orgId(org != null ? org.getId() : null)
                .eventCode("MONITOR_URL_DOWN")
                .targetRole(TargetRole.ADMIN)
                .recipientEmail(createdBy.getEmail())
                .recipientName(getUserDisplayName(createdBy))
                .templateVariables(variables)
                .subject(getSubjectForUrlDown(url, org))
                .build();
    }

    /**
     * Build notification event for URL RECOVERED alert
     */
    @Override
    public NotificationEventData buildUrlRecoveredEvent(MonitorUrl url, MonitorIncident incident) {
        Organisation org = url.getOrganisation();
        User createdBy = url.getCreatedBy();

        long durationSeconds = incident.getDurationSeconds() != null ? incident.getDurationSeconds() : 0;

        Map<String, Object> variables = new HashMap<>();
        variables.put("urlName", url.getName());
        variables.put("urlAddress", url.getUrl());
        variables.put("recoveryTime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        variables.put("downtimeDuration", formatDuration(durationSeconds));
        variables.put("organisationName", org != null ? org.getName() : "System");
        variables.put("systemName", "SellsHRMS");
        variables.put("dashboardUrl", "/monitor/urls/" + url.getId());

        return NotificationEventData.builder()
                .orgId(org != null ? org.getId() : null)
                .eventCode("MONITOR_URL_RECOVERED")
                .targetRole(TargetRole.ADMIN)
                .recipientEmail(createdBy.getEmail())
                .recipientName(getUserDisplayName(createdBy))
                .templateVariables(variables)
                .subject(getSubjectForUrlRecovered(url, org))
                .build();
    }

    /**
     * Build notification for SUPER_ADMIN (all organisations)
     */
    @Override
    public NotificationEventData buildSuperAdminUrlDownEvent(MonitorUrl url, MonitorIncident incident) {
        Organisation org = url.getOrganisation();

        Map<String, Object> variables = new HashMap<>();
        variables.put("urlName", url.getName());
        variables.put("urlAddress", url.getUrl());
        variables.put("downTime", incident.getStartedAt().format(DATE_TIME_FORMATTER));
        variables.put("cause", incident.getCause() != null ? incident.getCause() : "Connection failed");
        variables.put("failureThreshold", url.getFailureThreshold());
        variables.put("organisationName", org != null ? org.getName() : "Unknown");
        variables.put("systemName", "SellsHRMS");

        return NotificationEventData.builder()
                .orgId(null) // System-level notification
                .eventCode("MONITOR_URL_DOWN")
                .targetRole(TargetRole.SUPERADMIN)
                .recipientEmail(getSuperAdminEmail()) // Need to fetch
                .recipientName("Super Admin")
                .templateVariables(variables)
                .build();
    }

    /**
     * Send notifications to all group members for a URL
     */
    @Override
    public void sendToGroupMembers(MonitorUrl url, MonitorIncident incident, String eventCode) {
        // Get all unique users from groups containing this URL
        List<User> members = getGroupMembersForUrl(url.getId());

        Organisation org = url.getOrganisation();

        for (User member : members) {
            // Skip if already notified (creator gets separate notification)
            if (member.getId().equals(url.getCreatedBy().getId())) {
                continue;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("urlName", url.getName());
            variables.put("urlAddress", url.getUrl());
            variables.put("organisationName", org != null ? org.getName() : "System");
            variables.put("dashboardUrl", "/monitor");

            if ("MONITOR_URL_DOWN".equals(eventCode)) {
                variables.put("downTime", incident.getStartedAt().format(DATE_TIME_FORMATTER));
                variables.put("cause", incident.getCause());
                variables.put("failureThreshold", url.getFailureThreshold());
            } else {
                variables.put("recoveryTime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
                variables.put("downtimeDuration", formatDuration(incident.getDurationSeconds()));
            }

            NotificationEventData eventData = NotificationEventData.builder()
                    .orgId(org != null ? org.getId() : null)
                    .eventCode(eventCode)
                    .targetRole(TargetRole.EMPLOYEE)
                    .recipientEmail(member.getEmail())
                    .recipientName(getUserDisplayName(member))
                    .templateVariables(variables)
                    .build();

            notificationEventPublisher.publish(eventData);
        }
    }

    // Helper methods
    private String getUserDisplayName(User user) {
        if (user.getEmployee() != null) {
            return user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName();
        }
        return user.getEmail().split("@")[0];
    }

    private String formatInterval(Integer seconds) {
        if (seconds == null)
            return "5 minutes";
        if (seconds < 60)
            return seconds + " seconds";
        if (seconds < 3600)
            return (seconds / 60) + " minutes";
        return (seconds / 3600) + " hours";
    }

    private String formatDuration(long seconds) {
        if (seconds < 60)
            return seconds + " seconds";
        if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") +
                    (remainingSeconds > 0 ? " " + remainingSeconds + " seconds" : "");
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + " hour" + (hours > 1 ? "s" : "") +
                (minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "");
    }

    private String getSubjectForUrlDown(MonitorUrl url, Organisation org) {
        if (org != null) {
            return "🔴 " + org.getName() + ": " + url.getName() + " is DOWN";
        }
        return "🔴 ALERT: " + url.getName() + " is DOWN";
    }

    private String getSubjectForUrlRecovered(MonitorUrl url, Organisation org) {
        if (org != null) {
            return "🟢 " + org.getName() + ": " + url.getName() + " is back UP";
        }
        return "🟢 RECOVERED: " + url.getName() + " is back UP";
    }

    private String getSuperAdminEmail() {
        // Fetch from database or configuration
        // Option 1: Configuration property
        // Option 2: Query user with system_role = 'SUPER_ADMIN'
        return "admin@sellspark.com";
    }

    /**
     * Get all unique users from groups containing this URL
     */
    private List<User> getGroupMembersForUrl(String urlId) {
        // Implement query to get all users from groups containing this URL
        return userRepository.findUsersByUrlId(urlId);
    }
}