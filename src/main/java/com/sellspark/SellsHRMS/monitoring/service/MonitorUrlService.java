package com.sellspark.SellsHRMS.monitoring.service;

import com.sellspark.SellsHRMS.monitoring.dto.*;
import java.util.List;

public interface MonitorUrlService {

    // ==================== URL CRUD ====================
    MonitorUrlDTO createUrl(CreateUrlRequest request, Long organisationId, Long createdBy);

    MonitorUrlDTO updateUrl(String id, UpdateUrlRequest request, Long organisationId, Long userId);

    void deleteUrl(String id, Long organisationId, Long userId);

    MonitorUrlDTO toggleActive(String id, Long organisationId, Long userId);

    // ==================== URL Queries ====================
    UrlListDTO getUrls(Long organisationId, Long userId, int page, int limit, String status, String search);

    UrlDetailDTO getUrlDetail(String id, Long organisationId, Long userId);

    // ==================== Dashboard ====================
    MonitorDashboardDTO getDashboardStats(Long organisationId, Long userId);

    // ==================== URL Actions ====================
    HttpCheckResult checkNow(String id, Long organisationId, Long userId);

    // ==================== Incidents ====================
    IncidentListDTO getIncidents(Long organisationId, Long userId, int page, int limit, Boolean resolved);

    List<GroupSummaryDTO> getGroupSummaries(Long organisationId);

    List<SlowestUrlDTO> getSlowestUrls(Long organisationId, int limit);

    ResponseTimeChartDTO getResponseTimeTrendForUrl(String urlId);

    ResponseTimeChartDTO getResponseTimeTrendForGroup(String groupId);

    UptimeTrendDTO getUptimeTrendForUrl(String urlId, int days);
}
