package com.sellspark.SellsHRMS.monitoring.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.monitoring.dto.GroupSummaryDTO;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;

public interface MonitorUrlRepository extends JpaRepository<MonitorUrl, String> {

        // ==================== Basic Queries ====================

        @Query("SELECT u FROM MonitorUrl u WHERE u.organisation.id = :orgId")
        Page<MonitorUrl> findByOrganisationId(@Param("orgId") Long orgId, Pageable pageable);

        List<MonitorUrl> findByOrganisationId(Long orgId);

        // CORRECTED: Use currentStatus, not status
        Page<MonitorUrl> findByOrganisationIdAndCurrentStatus(@Param("orgId") Long orgId,
                        @Param("currentStatus") MonitorUrl.Status currentStatus, Pageable pageable);

        // CORRECTED: List version
        List<MonitorUrl> findByOrganisationIdAndCurrentStatus(@Param("orgId") Long orgId,
                        @Param("currentStatus") MonitorUrl.Status currentStatus);

        // ==================== Count Queries ====================

        long countByOrganisationIdAndIsActiveTrue(Long orgId);

        long countByOrganisationId(Long orgId);

        long countByOrganisationIdAndCurrentStatus(Long orgId, MonitorUrl.Status currentStatus);

        // ==================== Custom Queries ====================

        @Query("SELECT COALESCE(AVG(u.uptimePercentage), 100) FROM MonitorUrl u WHERE " +
                        "(:orgId IS NULL OR u.organisation.id = :orgId)")
        Double getAverageUptime(@Param("orgId") Long orgId);

        @Query("SELECT COUNT(DISTINCT g) FROM MonitorGroup g WHERE " +
                        "(:orgId IS NULL OR g.organisation.id = :orgId)")
        Long countGroupsByOrganisationId(@Param("orgId") Long orgId);

        @Query("SELECT u FROM MonitorUrl u WHERE " +
                        "(:orgId IS NULL OR u.organisation.id = :orgId) " +
                        "ORDER BY u.lastCheckedAt DESC")
        Page<MonitorUrl> findRecentByOrganisationId(@Param("orgId") Long orgId, Pageable pageable);

        @Query("SELECT DISTINCT u FROM MonitorUrl u " +
                        "LEFT JOIN FETCH u.organisation " +
                        "LEFT JOIN FETCH u.createdBy " +
                        "WHERE u.isActive = true AND " +
                        "(:orgId IS NULL OR u.organisation.id = :orgId) AND " +
                        "(u.lastCheckedAt IS NULL OR " +
                        "FUNCTION('TIMESTAMPDIFF', SECOND, u.lastCheckedAt, CURRENT_TIMESTAMP) >= u.checkInterval)")
        Page<MonitorUrl> findUrlsDueForCheck(@Param("orgId") Long orgId, Pageable pageable);

        // For dashboard - get URLs by status list
        @Query("SELECT u FROM MonitorUrl u WHERE u.organisation.id = :orgId AND u.currentStatus IN :statuses")
        List<MonitorUrl> findByOrganisationIdAndStatusIn(@Param("orgId") Long orgId,
                        @Param("statuses") List<MonitorUrl.Status> statuses);

        // Search URLs by name or URL
        @Query("SELECT u FROM MonitorUrl u WHERE u.organisation.id = :orgId AND " +
                        "(LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.url) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<MonitorUrl> searchByOrganisationId(@Param("orgId") Long orgId,
                        @Param("search") String search,
                        Pageable pageable);

        @Query("""
                            SELECT m
                            FROM MonitorUrl m
                            WHERE m.organisation.id = :organisationId
                              AND m.id NOT IN :excludedIds
                        """)
        List<MonitorUrl> findByOrganisationIdAndIdNotIn(
                        @Param("organisationId") Long organisationId,
                        @Param("excludedIds") List<String> excludedIds);

        List<MonitorUrl> findByOrganisationIdAndNameContainingIgnoreCaseAndIdNotIn(
                        Long organisationId, String name, List<String> excludedIds);

        @Query(value = "SELECT " +
                        "g.id, " +
                        "g.name, " +
                        "COUNT(DISTINCT gu.url_id) as totalUrls, " +
                        "COALESCE(AVG(u.uptime_percentage), 0) as avgUptime, " +
                        "SUM(CASE WHEN i.is_resolved = 0 THEN 1 ELSE 0 END) as activeIncidents, " +
                        "COUNT(DISTINCT gm.user_id) as totalMembers " +
                        "FROM monitor_groups g " +
                        "LEFT JOIN monitor_group_urls gu ON gu.group_id = g.id " +
                        "LEFT JOIN monitor_urls u ON u.id = gu.url_id " +
                        "LEFT JOIN monitor_incidents i ON i.url_id = u.id AND i.is_resolved = 0 " +
                        "LEFT JOIN monitor_group_members gm ON gm.group_id = g.id " +
                        "WHERE g.organisation_id = :orgId " +
                        "GROUP BY g.id, g.name", nativeQuery = true)
        List<Object[]> getGroupSummaryRawData(@Param("orgId") Long orgId);

        @Query("SELECT u FROM MonitorUrl u WHERE u.organisation.id = :orgId AND u.lastResponseTime IS NOT NULL " +
                        "ORDER BY u.lastResponseTime DESC")
        Page<MonitorUrl> findSlowestUrls(@Param("orgId") Long orgId, Pageable pageable);

}