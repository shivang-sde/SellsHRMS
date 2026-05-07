package com.sellspark.SellsHRMS.monitoring.controller;

import com.sellspark.SellsHRMS.dto.common.ApiResponse;
import com.sellspark.SellsHRMS.monitoring.dto.*;
import com.sellspark.SellsHRMS.monitoring.service.MonitorUrlService;

import jakarta.validation.Valid;

import com.sellspark.SellsHRMS.monitoring.service.MonitorGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorRestController {

    private final MonitorUrlService monitorUrlService;
    private final MonitorGroupService monitorGroupService;

    // ==================== DASHBOARD ====================

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MonitorDashboardDTO>> getDashboardStats(
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        MonitorDashboardDTO dashboard = monitorUrlService.getDashboardStats(organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Dashboard data fetched", dashboard));
    }

    @GetMapping("/groups/summary")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<GroupSummaryDTO>>> getGroupSummaries(
            @RequestParam Long organisationId) {
        return ResponseEntity.ok(ApiResponse.ok("Group summaries",
                monitorUrlService.getGroupSummaries(organisationId)));
    }

    @GetMapping("/analytics/url/{urlId}/response-trend")
    public ResponseEntity<ApiResponse<ResponseTimeChartDTO>> getUrlResponseTrend(
            @PathVariable String urlId) {
        return ResponseEntity.ok(ApiResponse.ok("URL response trend",
                monitorUrlService.getResponseTimeTrendForUrl(urlId)));
    }

    @GetMapping("/analytics/group/{groupId}/response-trend")
    public ResponseEntity<ApiResponse<ResponseTimeChartDTO>> getGroupResponseTrend(
            @PathVariable String groupId) {
        return ResponseEntity.ok(ApiResponse.ok("Group response trend",
                monitorUrlService.getResponseTimeTrendForGroup(groupId)));
    }

    @GetMapping("/analytics/url/{urlId}/uptime-trend")
    public ResponseEntity<ApiResponse<UptimeTrendDTO>> getUptimeTrend(
            @PathVariable String urlId,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.ok("Uptime trend",
                monitorUrlService.getUptimeTrendForUrl(urlId, days)));
    }

    @GetMapping("/slowest-urls")
    public ResponseEntity<ApiResponse<List<SlowestUrlDTO>>> getSlowestUrls(
            @RequestParam Long organisationId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.ok("Slowest URLs",
                monitorUrlService.getSlowestUrls(organisationId, limit)));
    }

    // ==================== URLS ====================

    @GetMapping("/urls")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UrlListDTO>> getUrls(
            @RequestParam Long organisationId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        UrlListDTO urls = monitorUrlService.getUrls(organisationId, userId, page, limit, status, search);
        return ResponseEntity.ok(ApiResponse.ok("URLs fetched", urls));
    }

    @GetMapping("/urls/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UrlDetailDTO>> getUrlDetail(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        UrlDetailDTO urlDetail = monitorUrlService.getUrlDetail(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("URL detail fetched", urlDetail));
    }

    @PostMapping("/urls")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<MonitorUrlDTO>> createUrl(
            @Valid @RequestBody CreateUrlRequest request,
            @RequestParam Long organisationId,
            @RequestParam Long createdBy) {

        MonitorUrlDTO created = monitorUrlService.createUrl(request, organisationId, createdBy);
        return ResponseEntity.ok(ApiResponse.ok("URL created successfully", created));
    }

    @PutMapping("/urls/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<MonitorUrlDTO>> updateUrl(
            @PathVariable String id,
            @Valid @RequestBody UpdateUrlRequest request,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        MonitorUrlDTO updated = monitorUrlService.updateUrl(id, request, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("URL updated successfully", updated));
    }

    @DeleteMapping("/urls/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        monitorUrlService.deleteUrl(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("URL deleted successfully", null));
    }

    @PostMapping("/urls/{id}/check")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HttpCheckResult>> checkNow(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        HttpCheckResult result = monitorUrlService.checkNow(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Check completed", result));
    }

    @PostMapping("/urls/{id}/toggle")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<MonitorUrlDTO>> toggleUrl(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        MonitorUrlDTO updated = monitorUrlService.toggleActive(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Status toggled", updated));
    }

    // ==================== GROUPS ====================

    @GetMapping("/groups")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<GroupListDTO>>> getGroups(
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        List<GroupListDTO> groups = monitorGroupService.getGroups(organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Groups fetched", groups));
    }

    @GetMapping("/groups/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<GroupDetailDTO>> getGroupDetail(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        GroupDetailDTO groupDetail = monitorGroupService.getGroupDetail(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Group detail fetched", groupDetail));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<GroupDTO>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @RequestParam Long organisationId,
            @RequestParam Long createdBy) {

        GroupDTO created = monitorGroupService.createGroup(request, organisationId, createdBy);
        return ResponseEntity.ok(ApiResponse.ok("Group created successfully", created));
    }

    @PutMapping("/groups/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<GroupDTO>> updateGroup(
            @PathVariable String id,
            @Valid @RequestBody UpdateGroupRequest request,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        GroupDTO updated = monitorGroupService.updateGroup(id, request, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Group updated successfully", updated));
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable String id,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        monitorGroupService.deleteGroup(id, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Group deleted successfully", null));
    }

    @GetMapping("/groups/{groupId}/available-urls")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UrlListItemDTO>>> getAvailableUrlsForGroup(
            @PathVariable String groupId,
            @RequestParam Long organisationId,
            @RequestParam Long userId,
            @RequestParam(required = false) String search) {

        List<UrlListItemDTO> urls = monitorGroupService.getAvailableUrlsForGroup(groupId, organisationId, userId,
                search);
        return ResponseEntity.ok(ApiResponse.ok("Available URLs fetched", urls));
    }

    @PostMapping("/groups/{groupId}/urls/{urlId}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addUrlToGroup(
            @PathVariable String groupId,
            @PathVariable String urlId,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        monitorGroupService.addUrlToGroup(groupId, urlId, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("URL added to group", null));
    }

    @DeleteMapping("/groups/{groupId}/urls/{urlId}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeUrlFromGroup(
            @PathVariable String groupId,
            @PathVariable String urlId,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        monitorGroupService.removeUrlFromGroup(groupId, urlId, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("URL removed from group", null));
    }

    @PostMapping("/groups/{groupId}/members/{memberId}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> addMemberToGroup(
            @PathVariable String groupId,
            @PathVariable Long memberId,
            @RequestParam Long organisationId,
            @RequestParam Long addedBy) {

        monitorGroupService.addMemberToGroup(groupId, memberId, organisationId, addedBy);
        return ResponseEntity.ok(ApiResponse.ok("Member added to group", null));
    }

    @DeleteMapping("/groups/{groupId}/members/{memberId}")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeMemberFromGroup(
            @PathVariable String groupId,
            @PathVariable Long memberId,
            @RequestParam Long organisationId,
            @RequestParam Long userId) {

        monitorGroupService.removeMemberFromGroup(groupId, memberId, organisationId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Member removed from group", null));
    }

    // ==================== INCIDENTS ====================

    @GetMapping("/incidents")
    @PreAuthorize("hasAnyAuthority('ORG_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<IncidentListDTO>> getIncidents(
            @RequestParam Long organisationId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Boolean resolved) {

        IncidentListDTO incidents = monitorUrlService.getIncidents(organisationId, userId, page, limit, resolved);
        return ResponseEntity.ok(ApiResponse.ok("Incidents fetched", incidents));
    }
}