package com.sellspark.SellsHRMS.monitoring.repository;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MonitorCheckRepository extends JpaRepository<MonitorCheck, String> {

        @Query("SELECT c FROM MonitorCheck c WHERE c.url.id = :urlId ORDER BY c.checkedAt DESC")
        Page<MonitorCheck> findLatestByUrlId(@Param("urlId") String urlId, Pageable pageable);

        List<MonitorCheck> findTop25ByUrlIdOrderByCheckedAtDesc(String urlId);

        @Query(value = "SELECT " +
                        "COUNT(*) as total, " +
                        "SUM(CASE WHEN c.is_up = true THEN 1 ELSE 0 END) as upCount " +
                        "FROM monitor_checks c " +
                        "WHERE c.url_id = :urlId AND c.checked_at >= :since", nativeQuery = true)
        Map<String, Object> getUptimeStats(@Param("urlId") String urlId, @Param("since") LocalDateTime since);

        @Modifying
        @Transactional
        @Query("DELETE FROM MonitorCheck c WHERE c.checkedAt < :cutoff")
        int deleteOldChecks(@Param("cutoff") LocalDateTime cutoff);

        @Query("SELECT AVG(c.responseTime) FROM MonitorCheck c WHERE " +
                        "c.url.organisation.id = :orgId AND c.isUp = true AND " +
                        "c.checkedAt >= :since")
        Integer getAverageResponseTimeSince(@Param("orgId") Long orgId,
                        @Param("since") LocalDateTime since);

        default Integer getAverageResponseTimeByOrganisationId(Long orgId) {
                if (orgId == null)
                        return 0;
                return getAverageResponseTimeSince(orgId, LocalDateTime.now().minusHours(24));
        }

        // FIXED: Use NATIVE QUERY for MySQL HOUR function
        @Query(value = "SELECT " +
                        "HOUR(c.checked_at) as hour, " +
                        "AVG(c.response_time) as avgTime " +
                        "FROM monitor_checks c " +
                        "INNER JOIN monitor_urls u ON c.url_id = u.id " +
                        "WHERE u.organisation_id = :orgId " +
                        "AND c.is_up = true " +
                        "AND c.checked_at >= DATE_SUB(NOW(), INTERVAL :hours HOUR) " +
                        "GROUP BY HOUR(c.checked_at) " +
                        "ORDER BY hour ASC", nativeQuery = true)
        List<Object[]> getResponseTimeTrendNative(@Param("orgId") Long orgId, @Param("hours") int hours);

        // Keep your existing JPQL for other uses
        @Query("SELECT FUNCTION('HOUR', c.checkedAt) as hour, AVG(c.responseTime) as avgTime " +
                        "FROM MonitorCheck c WHERE c.url.organisation.id = :orgId " +
                        "AND c.checkedAt >= :since GROUP BY FUNCTION('HOUR', c.checkedAt) " +
                        "ORDER BY hour ASC")
        List<Object[]> getResponseTimeTrend(@Param("orgId") Long orgId, @Param("since") LocalDateTime since);

        default List<Object[]> getResponseTimeTrend(Long orgId, int hours) {
                if (orgId == null)
                        return List.of();
                // Use native query for better compatibility
                return getResponseTimeTrendNative(orgId, hours);
        }

        @Query("SELECT COUNT(c) FROM MonitorCheck c WHERE " +
                        "(:orgId IS NULL OR c.url.organisation.id = :orgId) AND " +
                        "(:isUp IS NULL OR c.isUp = :isUp)")
        Long countChecksByStatus(@Param("orgId") Long orgId, @Param("isUp") Boolean isUp);

        @Query("SELECT AVG(c.responseTime) FROM MonitorCheck c WHERE " +
                        "(:orgId IS NULL OR c.url.organisation.id = :orgId) AND c.isUp = true")
        Double getAverageResponseTime(@Param("orgId") Long orgId);

        // Daily uptime percentage for a specific URL over last N days
        @Query(value = "SELECT DATE(c.checked_at) as day, " +
                        "ROUND(SUM(CASE WHEN c.is_up = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as uptime " +
                        "FROM monitor_checks c " +
                        "WHERE c.url_id = :urlId AND c.checked_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +
                        "GROUP BY DATE(c.checked_at) ORDER BY day ASC", nativeQuery = true)
        List<Object[]> getDailyUptimeTrend(@Param("urlId") String urlId, @Param("days") int days);

        // Response time trend for a single URL (last 24 hours)
        @Query(value = "SELECT HOUR(c.checked_at) as hour, AVG(c.response_time) as avgTime " +
                        "FROM monitor_checks c " +
                        "WHERE c.url_id = :urlId AND c.is_up = true " +
                        "AND c.checked_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                        "GROUP BY HOUR(c.checked_at) ORDER BY hour ASC", nativeQuery = true)
        List<Object[]> getResponseTimeTrendForUrl(@Param("urlId") String urlId);

        // Response time trend for a group (average of all URLs in group)
        @Query(value = "SELECT HOUR(c.checked_at) as hour, AVG(c.response_time) as avgTime " +
                        "FROM monitor_checks c " +
                        "INNER JOIN monitor_urls u ON c.url_id = u.id " +
                        "INNER JOIN monitor_group_urls gu ON gu.url_id = u.id " +
                        "WHERE gu.group_id = :groupId AND c.is_up = true " +
                        "AND c.checked_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                        "GROUP BY HOUR(c.checked_at) ORDER BY hour ASC", nativeQuery = true)
        List<Object[]> getResponseTimeTrendForGroup(@Param("groupId") String groupId);

}