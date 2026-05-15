package com.sellspark.SellsHRMS.monitoring.service.impl;

import com.sellspark.SellsHRMS.entity.User;
import com.sellspark.SellsHRMS.monitoring.dto.*;
import com.sellspark.SellsHRMS.monitoring.entity.*;
import com.sellspark.SellsHRMS.monitoring.repository.*;
import com.sellspark.SellsHRMS.monitoring.service.HttpClientService;
import com.sellspark.SellsHRMS.monitoring.service.MonitorGroupService;
import com.sellspark.SellsHRMS.monitoring.service.MonitorUrlService;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MonitorUrlServiceImpl implements MonitorUrlService {

    private final MonitorUrlRepository urlRepository;
    private final MonitorCheckRepository checkRepository;
    private final MonitorIncidentRepository incidentRepository;
    private final MonitorGroupUrlRepository groupUrlRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
    private final HttpClientService httpClientService;

    private final MonitorGroupService monitorGroupService;

    @Override
    public MonitorUrlDTO createUrl(CreateUrlRequest request, Long organisationId, Long createdBy) {
        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new RuntimeException("Organisation not found: " + organisationId));
        User creator = userRepository.findById(createdBy)
                .orElseThrow(() -> new RuntimeException("User not found: " + createdBy));

        MonitorUrl url = MonitorUrl.builder()
                .id(UUID.randomUUID().toString())
                .organisation(organisation)
                .name(request.getName())
                .url(request.getUrl())
                .method(MonitorUrl.RequestMethod.valueOf(request.getMethod()))
                .checkInterval(request.getCheckInterval() != null ? request.getCheckInterval() : 300)
                .timeout(request.getTimeout() != null ? request.getTimeout() : 30)
                .failureThreshold(request.getFailureThreshold() != null ? request.getFailureThreshold() : 3)
                .isActive(true)
                .currentStatus(MonitorUrl.Status.pending)
                .consecutiveFailures(0)
                .uptimePercentage(BigDecimal.valueOf(100.00))
                .createdBy(creator)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MonitorUrl saved = urlRepository.save(url);

        // Add to group if specified
        if (request.getGroupId() != null && !request.getGroupId().isEmpty()) {
            try {
                monitorGroupService.addUrlToGroup(request.getGroupId(), saved.getId(), organisationId, createdBy);
            } catch (Exception e) {
                log.warn("Failed to add URL to group: {}", e.getMessage());
            }
        }

        log.info("URL created: {} by {}", saved.getName(), creator.getEmail());
        return convertToMonitorUrlDTO(saved);
    }

    @Override
    public MonitorUrlDTO updateUrl(String id, UpdateUrlRequest request, Long organisationId, Long userId) {
        MonitorUrl url = getUrlEntity(id, organisationId);

        if (request.getName() != null)
            url.setName(request.getName());
        if (request.getUrl() != null)
            url.setUrl(request.getUrl());
        if (request.getMethod() != null)
            url.setMethod(MonitorUrl.RequestMethod.valueOf(request.getMethod()));
        if (request.getCheckInterval() != null)
            url.setCheckInterval(request.getCheckInterval());
        if (request.getTimeout() != null)
            url.setTimeout(request.getTimeout());
        if (request.getFailureThreshold() != null)
            url.setFailureThreshold(request.getFailureThreshold());
        if (request.getIsActive() != null)
            url.setIsActive(request.getIsActive());

        if (request.getGroupId() != null && !request.getGroupId().isEmpty()) {
            try {
                monitorGroupService.addUrlToGroup(request.getGroupId(), url.getId(), organisationId, userId);
            } catch (Exception e) {
                log.warn("Failed to update URL in group: {}", e.getMessage());
            }
        }

        url.setUpdatedAt(LocalDateTime.now());

        MonitorUrl updated = urlRepository.save(url);
        log.info("URL updated: {} by {}", updated.getName(), userId);
        return convertToMonitorUrlDTO(updated);
    }

    @Override
    public void deleteUrl(String id, Long organisationId, Long userId) {
        MonitorUrl url = getUrlEntity(id, organisationId);
        urlRepository.delete(url);
        log.info("URL deleted: {} by {}", url.getName(), userId);
    }

    @Override
    public MonitorUrlDTO toggleActive(String id, Long organisationId, Long userId) {
        MonitorUrl url = getUrlEntity(id, organisationId);
        url.setIsActive(!url.getIsActive());
        url.setUpdatedAt(LocalDateTime.now());
        MonitorUrl updated = urlRepository.save(url);
        log.info("URL {} toggled to {} by {}", url.getName(),
                url.getIsActive() ? "active" : "inactive", userId);
        return convertToMonitorUrlDTO(updated);
    }

    @Override
    public UrlListDTO getUrls(Long organisationId, Long userId, int page, int limit,
            String status, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<MonitorUrl> urlPage;
        if (status != null && !status.isEmpty()) {
            MonitorUrl.Status urlStatus = MonitorUrl.Status.valueOf(status);
            urlPage = urlRepository.findByOrganisationIdAndCurrentStatus(organisationId, urlStatus, pageable);
        } else {
            urlPage = urlRepository.findByOrganisationId(organisationId, pageable);
        }

        List<MonitorUrl> urls = urlPage.getContent();
        if (search != null && !search.isEmpty()) {
            urls = urls.stream()
                    .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase()) ||
                            u.getUrl().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<UrlListItemDTO> urlItems = urls.stream()
                .map(this::convertToUrlListItemDTO)
                .collect(Collectors.toList());

        return UrlListDTO.builder()
                .urls(urlItems)
                .pagination(PaginationDTO.builder()
                        .page(page)
                        .limit(limit)
                        .total((long) urlItems.size())
                        .totalPages((int) Math.ceil((double) urlItems.size() / limit))
                        .build())
                .build();
    }

    @Override
    public UrlDetailDTO getUrlDetail(String id, Long organisationId, Long userId) {
        MonitorUrl url = getUrlEntity(id, organisationId);

        Pageable checksPageable = PageRequest.of(0, 5);
        List<MonitorCheck> checks = checkRepository.findLatestByUrlId(id, checksPageable).getContent();
        List<MonitorIncident> incidents = incidentRepository.findByUrlIdOrderByStartedAtDesc(id);
        List<MonitorGroup> groups = groupUrlRepository.findGroupsByUrlId(id);

        return UrlDetailDTO.builder()
                .url(convertToMonitorUrlDTO(url))
                .checks(checks.stream().map(this::convertToCheckDTO).collect(Collectors.toList()))
                .incidents(incidents.stream().map(this::convertToIncidentDTO).collect(Collectors.toList()))
                .groups(groups.stream().map(this::convertToGroupBasicDTO).collect(Collectors.toList()))
                .build();
    }

    @Override
    public MonitorDashboardDTO getDashboardStats(Long organisationId, Long userId) {
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalUrls(urlRepository.countByOrganisationId(organisationId))
                .upCount(urlRepository.countByOrganisationIdAndCurrentStatus(organisationId, MonitorUrl.Status.up))
                .downCount(urlRepository.countByOrganisationIdAndCurrentStatus(organisationId, MonitorUrl.Status.down))
                .pendingCount(
                        urlRepository.countByOrganisationIdAndCurrentStatus(organisationId, MonitorUrl.Status.pending))
                .avgUptime(urlRepository.getAverageUptime(organisationId))
                .totalGroups(urlRepository.countGroupsByOrganisationId(organisationId))
                .activeIncidents(incidentRepository.countActiveByOrganisationId(organisationId))
                .avgResponseTime(checkRepository.getAverageResponseTimeByOrganisationId(organisationId))
                .build();

        List<ActiveIncidentDTO> activeIncidents = incidentRepository
                .findActiveByOrganisationId(organisationId)
                .stream()
                .map(this::convertToActiveIncidentDTO)
                .collect(Collectors.toList());

        List<UrlListItemDTO> downUrls = urlRepository
                .findByOrganisationIdAndCurrentStatus(organisationId, MonitorUrl.Status.down, PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(this::convertToUrlListItemDTO)
                .collect(Collectors.toList());

        List<UrlListItemDTO> recentUrls = urlRepository
                .findRecentByOrganisationId(organisationId, PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(this::convertToUrlListItemDTO)
                .collect(Collectors.toList());

        ResponseTimeChartDTO responseTimeData = getResponseTimeChartData(organisationId);

        return MonitorDashboardDTO.builder()
                .stats(stats)
                .activeIncidents(activeIncidents)
                .downUrls(downUrls)
                .recentUrls(recentUrls)
                .responseTimeData(responseTimeData)
                .build();
    }

    @Override
    public HttpCheckResult checkNow(String id, Long organisationId, Long userId) {
        MonitorUrl url = getUrlEntity(id, organisationId);
        return httpClientService.checkUrl(url);
    }

    @Override
    public IncidentListDTO getIncidents(Long organisationId, Long userId, int page, int limit, Boolean resolved) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Page<MonitorIncident> incidentPage;
        if (resolved != null) {
            incidentPage = incidentRepository.findByOrganisationIdAndResolved(organisationId, resolved, pageable);
        } else {
            incidentPage = incidentRepository.findByOrganisationId(organisationId, pageable);
        }

        return IncidentListDTO.builder()
                .incidents(incidentPage.getContent().stream()
                        .map(this::convertToIncidentDTO)
                        .collect(Collectors.toList()))
                .pagination(PaginationDTO.builder()
                        .page(page)
                        .limit(limit)
                        .total(incidentPage.getTotalElements())
                        .totalPages(incidentPage.getTotalPages())
                        .build())
                .build();
    }

    // ==================== Private Helper Methods ====================

    private MonitorUrl getUrlEntity(String id, Long organisationId) {
        MonitorUrl url = urlRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found: " + id));

        if (!url.getOrganisation().getId().equals(organisationId)) {
            throw new RuntimeException("Access denied: URL belongs to different organisation");
        }
        return url;
    }

    // ==================== Converter Methods ====================

    private MonitorUrlDTO convertToMonitorUrlDTO(MonitorUrl url) {
        return MonitorUrlDTO.builder()
                .id(url.getId())
                .name(url.getName())
                .url(url.getUrl())
                .method(url.getMethod().name())
                .checkInterval(url.getCheckInterval())
                .timeout(url.getTimeout())
                .failureThreshold(url.getFailureThreshold())
                .currentStatus(url.getCurrentStatus().name())
                .uptimePercentage(url.getUptimePercentage().doubleValue())
                .lastResponseTime(url.getLastResponseTime())
                .lastStatusCode(url.getLastStatusCode())
                .lastError(url.getLastError())
                .lastCheckedAt(url.getLastCheckedAt())
                .isActive(url.getIsActive())
                .createdAt(url.getCreatedAt())
                .createdByName(url.getCreatedBy() != null ? url.getCreatedBy().getEmail() : null)
                .build();
    }

    private UrlListItemDTO convertToUrlListItemDTO(MonitorUrl url) {
        return UrlListItemDTO.builder()
                .id(url.getId())
                .name(url.getName())
                .url(url.getUrl())
                .currentStatus(url.getCurrentStatus().name())
                .uptimePercentage(url.getUptimePercentage().doubleValue())
                .lastResponseTime(url.getLastResponseTime())
                .lastCheckedAt(url.getLastCheckedAt())
                .isActive(url.getIsActive())
                .checkInterval(url.getCheckInterval())
                .build();
    }

    private CheckDTO convertToCheckDTO(MonitorCheck check) {
        return CheckDTO.builder()
                .id(check.getId())
                .statusCode(check.getStatusCode())
                .responseTime(check.getResponseTime())
                .isUp(check.getIsUp())
                .error(check.getError())
                .checkedAt(check.getCheckedAt())
                .build();
    }

    private IncidentDTO convertToIncidentDTO(MonitorIncident incident) {
        return IncidentDTO.builder()
                .id(incident.getId())
                .urlId(incident.getUrl().getId())
                .urlName(incident.getUrl().getName())
                .url(incident.getUrl().getUrl())
                .durationSeconds(incident.getDurationSeconds())
                .cause(incident.getCause())
                .resolved(incident.getIsResolved())
                .notificationSent(incident.getNotificationSent())
                .build();
    }

    private ActiveIncidentDTO convertToActiveIncidentDTO(MonitorIncident incident) {
        return ActiveIncidentDTO.builder()
                .id(incident.getId())
                .urlId(incident.getUrl().getId())
                .urlName(incident.getUrl().getName())
                .url(incident.getUrl().getUrl())
                .startedAt(incident.getStartedAt())
                .cause(incident.getCause())
                .build();
    }

    private GroupBasicDTO convertToGroupBasicDTO(MonitorGroup group) {
        return GroupBasicDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }

    private ResponseTimeChartDTO getResponseTimeChartData(Long organisationId) {
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        try {
            // Get last 24 hours of response time data
            List<Object[]> results = checkRepository.getResponseTimeTrend(organisationId, 24);

            if (results != null && !results.isEmpty()) {
                for (Object[] row : results) {
                    // row[0] = hour (0-23)
                    // row[1] = average response time
                    Number hourNum = (Number) row[0];
                    Number avgTimeNum = (Number) row[1];

                    if (hourNum != null && avgTimeNum != null) {
                        int hour = hourNum.intValue();
                        int avgTime = avgTimeNum.intValue();

                        labels.add(String.format("%02d:00", hour));
                        values.add(avgTime);
                    }
                }
            }

            // If no data yet, return placeholder
            if (labels.isEmpty()) {
                labels.add("No Data");
                values.add(0);
            }

        } catch (Exception e) {
            log.error("Failed to get response time chart data: {}", e.getMessage(), e);
            labels.add("Error");
            values.add(0);
        }

        return ResponseTimeChartDTO.builder()
                .labels(labels)
                .values(values)
                .build();
    }

    @Override
    public List<GroupSummaryDTO> getGroupSummaries(Long organisationId) {
        List<Object[]> results = urlRepository.getGroupSummaryRawData(organisationId);
        List<GroupSummaryDTO> summaries = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return summaries;
        }

        for (Object[] row : results) {
            try {
                // row[0] = groupId (String)
                // row[1] = groupName (String)
                // row[2] = totalUrls (Number)
                // row[3] = avgUptime (Number)
                // row[4] = activeIncidents (Number)
                // row[5] = totalMembers (Number)

                String groupId = (String) row[0];
                String groupName = (String) row[1];
                Long totalUrls = ((Number) row[2]).longValue();
                Double avgUptime = ((Number) row[3]).doubleValue();
                Long activeIncidents = ((Number) row[4]).longValue();
                Long totalMembers = ((Number) row[5]).longValue();

                // Determine health status based on uptime
                String healthStatus;
                if (avgUptime >= 99) {
                    healthStatus = "excellent";
                } else if (avgUptime >= 95) {
                    healthStatus = "good";
                } else if (avgUptime >= 85) {
                    healthStatus = "warning";
                } else {
                    healthStatus = "critical";
                }

                GroupSummaryDTO dto = GroupSummaryDTO.builder()
                        .groupId(groupId)
                        .groupName(groupName)
                        .totalUrls(totalUrls)
                        .avgUptime(Math.round(avgUptime * 100.0) / 100.0) // Round to 2 decimal places
                        .activeIncidents(activeIncidents)
                        .totalMembers(totalMembers)
                        .healthStatus(healthStatus)
                        .build();

                summaries.add(dto);

            } catch (Exception e) {
                log.error("Error converting group summary row: {}", e.getMessage());
            }
        }

        return summaries;
    }

    @Override
    public List<SlowestUrlDTO> getSlowestUrls(Long organisationId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<MonitorUrl> slowestPage = urlRepository.findSlowestUrls(organisationId, pageable);

        return slowestPage.getContent().stream()
                .map(url -> SlowestUrlDTO.builder()
                        .urlId(url.getId())
                        .name(url.getName())
                        .url(url.getUrl())
                        .avgResponseTime(
                                url.getLastResponseTime() != null ? url.getLastResponseTime().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ResponseTimeChartDTO getResponseTimeTrendForUrl(String urlId) {
        List<Object[]> results = checkRepository.getResponseTimeTrendForUrl(urlId);
        return buildChartDTO(results);
    }

    @Override
    public ResponseTimeChartDTO getResponseTimeTrendForGroup(String groupId) {
        List<Object[]> results = checkRepository.getResponseTimeTrendForGroup(groupId);
        return buildChartDTO(results);
    }

    @Override
    public UptimeTrendDTO getUptimeTrendForUrl(String urlId, int days) {
        List<Object[]> results = checkRepository.getDailyUptimeTrend(urlId, days);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Object[] row : results) {
            java.sql.Date date = (java.sql.Date) row[0];
            labels.add(new java.text.SimpleDateFormat("dd MMM").format(date));
            values.add(((Number) row[1]).doubleValue());
        }
        return UptimeTrendDTO.builder().labels(labels).values(values).build();
    }

    private ResponseTimeChartDTO buildChartDTO(List<Object[]> results) {
        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        for (Object[] row : results) {
            int hour = ((Number) row[0]).intValue();
            int avg = ((Number) row[1]).intValue();
            labels.add(String.format("%02d:00", hour));
            values.add(avg);
        }
        if (labels.isEmpty()) {
            labels.add("No data");
            values.add(0);
        }
        return ResponseTimeChartDTO.builder().labels(labels).values(values).build();
    }
}