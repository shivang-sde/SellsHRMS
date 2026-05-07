package com.sellspark.SellsHRMS.monitoring.repository;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorGroup;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorGroupUrl;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorGroupUrlRepository extends JpaRepository<MonitorGroupUrl, String> {

        @Query("SELECT gu.group FROM MonitorGroupUrl gu WHERE gu.url.id = :urlId")
        List<MonitorGroup> findGroupsByUrlId(@Param("urlId") String urlId);

        @Query("SELECT gu.url FROM MonitorGroupUrl gu WHERE gu.group.id = :groupId")
        List<MonitorUrl> findUrlsByGroupId(@Param("groupId") String groupId);

        int countUrlsByGroupId(String groupId);

        int countByGroupId(String groupId);

        @Query("SELECT mgu.url.id FROM MonitorGroupUrl mgu WHERE mgu.group.id = :groupId")
        List<String> findUrlIdsByGroupId(@Param("groupId") String groupId);

        boolean existsByGroupIdAndUrlId(String groupId, String urlId);

        @Modifying
        void deleteByGroupIdAndUrlId(String groupId, String urlId);

        @Modifying
        @Query("INSERT INTO MonitorGroupUrl(id, group, url, createdAt) " +
                        "VALUES(:id, (SELECT g FROM MonitorGroup g WHERE g.id = :groupId), " +
                        "(SELECT u FROM MonitorUrl u WHERE u.id = :urlId), CURRENT_TIMESTAMP)")
        void addUrlToGroup(@Param("groupId") String groupId,
                        @Param("urlId") String urlId,
                        @Param("id") String id);
}