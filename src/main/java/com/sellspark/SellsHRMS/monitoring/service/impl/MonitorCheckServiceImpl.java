package com.sellspark.SellsHRMS.monitoring.service.impl;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.monitoring.dto.HttpCheckResult;
import com.sellspark.SellsHRMS.monitoring.entity.*;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl.Status;
import com.sellspark.SellsHRMS.monitoring.repository.*;
import com.sellspark.SellsHRMS.monitoring.service.HttpClientService;
import com.sellspark.SellsHRMS.monitoring.service.MonitorCheckService;
import com.sellspark.SellsHRMS.monitoring.service.MonitorNotificationEventBuilder;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;
import com.sellspark.SellsHRMS.notification.event.NotificationEventData;
import com.sellspark.SellsHRMS.notification.event.NotificationEventPublisher;
import com.sellspark.SellsHRMS.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorCheckServiceImpl implements MonitorCheckService {

    private final MonitorUrlRepository urlRepository;
    private final MonitorCheckRepository checkRepository;
    private final MonitorIncidentRepository incidentRepository;
    private final HttpClientService httpClientService;
    private final NotificationEventPublisher notificationEventPublisher;
    private final MonitorNotificationEventBuilder notificationEventBuilder;
    private final UserRepository userRepository;

    private Status determineNewStatus(boolean isUp, int consecutiveFailures, MonitorUrl url) {
        if (isUp) {
            return Status.up;
        }
        if (consecutiveFailures >= url.getFailureThreshold()) {
            return Status.down;
        }
        return url.getCurrentStatus();
    }

    private BigDecimal calculateUptimePercentage(String urlId) {
        try {
            Map<String, Object> stats = checkRepository.getUptimeStats(urlId, LocalDateTime.now().minusDays(1));
            long total = stats.get("total") != null ? ((Number) stats.get("total")).longValue() : 0;
            long upCount = stats.get("upCount") != null ? ((Number) stats.get("upCount")).longValue() : 0;
            if (total == 0) {
                return BigDecimal.valueOf(100.00);
            }
            double percentage = (upCount * 100.0) / total;
            return BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Failed to calculate uptime for URL: {}", urlId, e);
            return BigDecimal.valueOf(100.00);
        }
    }

    @Transactional
    @Override
    public void processCheck(MonitorUrl url) {
        log.debug("Checking URL: {} ({})", url.getName(), url.getUrl());

        // 1. Perform HTTP check
        HttpCheckResult result = httpClientService.checkUrl(url);

        // 2. Save check record
        MonitorCheck check = MonitorCheck.builder()
                .url(url)
                .statusCode(result.getStatusCode())
                .responseTime(result.getResponseTime())
                .isUp(result.isUp())
                .error(result.getError())
                .build();
        checkRepository.save(check);

        // 3. Update URL statistics
        Status prevStatus = url.getCurrentStatus();
        int newConsecutiveFailures = result.isUp() ? 0 : url.getConsecutiveFailures() + 1;
        Status newStatus = determineNewStatus(result.isUp(), newConsecutiveFailures, url);

        BigDecimal uptime = calculateUptimePercentage(url.getId());

        url.setCurrentStatus(newStatus);
        url.setLastCheckedAt(LocalDateTime.now());
        url.setLastResponseTime(result.getResponseTime());
        url.setLastStatusCode(result.getStatusCode());
        url.setLastError(result.getError());
        url.setConsecutiveFailures(newConsecutiveFailures);
        url.setUptimePercentage(uptime);
        urlRepository.save(url);

        // 4. Handle state changes with email notifications
        handleStateChangeAndNotify(url, prevStatus, newStatus, result);
    }

    @Override
    public void handleStateChangeAndNotify(MonitorUrl url, Status prevStatus, Status newStatus,
            HttpCheckResult result) {

        // CASE 1: URL went DOWN
        if (newStatus == Status.down && prevStatus != Status.down) {
            log.info("URL went DOWN: {}", url.getName());

            // Create incident record
            MonitorIncident incident = MonitorIncident.builder()
                    .url(url)
                    .cause(result.getError() != null ? result.getError() : "HTTP " + result.getStatusCode())
                    .notificationSent(true)
                    .build();
            incidentRepository.save(incident);

            // CRITICAL FIX: Initialize all lazy loaded data before publishing
            // This ensures data is loaded while still in the transactional context
            initializeLazyData(url, incident);

            // BUILD AND PUBLISH EMAIL NOTIFICATIONS
            NotificationEventData downEventToCreator = notificationEventBuilder
                    .buildUrlDownEvent(url, incident);
            if (downEventToCreator != null) {
                notificationEventPublisher.publish(downEventToCreator);
            }

            notificationEventBuilder.sendToGroupMembers(url, incident, "MONITOR_URL_DOWN");
            notifySuperAdminsForUrlDown(url, incident);
        }

        // CASE 2: URL RECOVERED
        else if (newStatus == Status.up && prevStatus == Status.down) {
            log.info("URL recovered: {}", url.getName());

            Optional<MonitorIncident> activeIncident = incidentRepository
                    .findByUrlIdAndIsResolvedFalse(url.getId());

            if (activeIncident.isPresent()) {
                MonitorIncident incident = activeIncident.get();
                incident.setEndedAt(LocalDateTime.now());
                incident.setDurationSeconds((int) ChronoUnit.SECONDS.between(
                        incident.getStartedAt(), LocalDateTime.now()));
                incident.setIsResolved(true);
                incident.setRecoveryNotificationSent(true);
                incidentRepository.save(incident);

                // CRITICAL FIX: Initialize all lazy loaded data before publishing
                initializeLazyData(url, incident);

                NotificationEventData recoveryEventToCreator = notificationEventBuilder
                        .buildUrlRecoveredEvent(url, incident);
                if (recoveryEventToCreator != null) {
                    notificationEventPublisher.publish(recoveryEventToCreator);
                }

                notificationEventBuilder.sendToGroupMembers(url, incident, "MONITOR_URL_RECOVERED");
                notifySuperAdminsForUrlRecovered(url, incident);
            }
        }
    }

    /**
     * CRITICAL: Initialize all lazy loaded entities while still in transaction
     * This prevents LazyInitializationException when the event listener processes
     * the notification asynchronously.
     */
    private void initializeLazyData(MonitorUrl url, MonitorIncident incident) {
        // Initialize URL's lazy loaded associations
        if (url.getOrganisation() != null) {
            url.getOrganisation().getName(); // Force initialization
            url.getOrganisation().getId(); // Also initialize ID
        }
        if (url.getCreatedBy() != null) {
            url.getCreatedBy().getEmail(); // Force initialization
            url.getCreatedBy().getId();
            if (url.getCreatedBy().getEmployee() != null) {
                url.getCreatedBy().getEmployee().getFirstName(); // Initialize employee if exists
            }
        }

        // Initialize incident's URL reference (already loaded via url param)
        if (incident.getUrl() != null) {
            incident.getUrl().getId();
        }

        log.debug("Lazy data initialized for URL: {}", url.getId());
    }

    @Override
    public void notifySuperAdminsForUrlDown(MonitorUrl url, MonitorIncident incident) {
        List<User> superAdmins = userRepository.findBySystemRole(User.SystemRole.SUPER_ADMIN);

        // Pre-fetch super admin data to avoid lazy loading issues in async listener
        for (User admin : superAdmins) {
            admin.getEmail();
            if (admin.getEmployee() != null) {
                admin.getEmployee().getFirstName();
            }
        }

        for (User superAdmin : superAdmins) {
            NotificationEventData superAdminEvent = NotificationEventData.builder()
                    .orgId(null)
                    .eventCode("MONITOR_URL_DOWN")
                    .targetRole(TargetRole.SUPERADMIN)
                    .recipientEmail(superAdmin.getEmail())
                    .recipientName("Super Admin")
                    .templateVariables(Map.of(
                            "urlName", url.getName(),
                            "urlAddress", url.getUrl(),
                            "organisationName",
                            url.getOrganisation() != null ? url.getOrganisation().getName() : "Unknown",
                            "downTime",
                            incident.getStartedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")),
                            "cause", incident.getCause()))
                    .build();
            notificationEventPublisher.publish(superAdminEvent);
        }
    }

    private void notifySuperAdminsForUrlRecovered(MonitorUrl url, MonitorIncident incident) {
        List<User> superAdmins = userRepository.findBySystemRole(User.SystemRole.SUPER_ADMIN);

        String downtimeDuration = formatDuration(incident.getDurationSeconds());

        for (User superAdmin : superAdmins) {
            NotificationEventData superAdminEvent = NotificationEventData.builder()
                    .orgId(null)
                    .eventCode("MONITOR_URL_RECOVERED")
                    .targetRole(TargetRole.SUPERADMIN)
                    .recipientEmail(superAdmin.getEmail())
                    .recipientName("Super Admin")
                    .templateVariables(Map.of(
                            "urlName", url.getName(),
                            "urlAddress", url.getUrl(),
                            "organisationName",
                            url.getOrganisation() != null ? url.getOrganisation().getName() : "Unknown",
                            "recoveryTime",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")),
                            "downtimeDuration", downtimeDuration))
                    .build();
            notificationEventPublisher.publish(superAdminEvent);
        }
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds == 0) {
            return "Less than a minute";
        }
        if (seconds < 60) {
            return seconds + " second" + (seconds > 1 ? "s" : "");
        }
        if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "") +
                    (remainingSeconds > 0 ? " " + remainingSeconds + " second" + (remainingSeconds > 1 ? "s" : "")
                            : "");
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + " hour" + (hours > 1 ? "s" : "") +
                (minutes > 0 ? " " + minutes + " minute" + (minutes > 1 ? "s" : "") : "");
    }
}