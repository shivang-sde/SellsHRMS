package com.sellspark.SellsHRMS.monitoring.repository;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorGroupMember;
import com.sellspark.SellsHRMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitorGroupMemberRepository extends JpaRepository<MonitorGroupMember, String> {

    @Query("SELECT m FROM MonitorGroupMember m JOIN FETCH m.user WHERE m.group.id = :groupId")
    List<MonitorGroupMember> findByGroupIdWithUser(@Param("groupId") String groupId);

    @Query("SELECT m.user FROM MonitorGroupMember m WHERE m.group.id = :groupId")
    List<User> findUsersByGroupId(@Param("groupId") String groupId);

    @Query("SELECT m.user.id FROM MonitorGroupMember m WHERE m.group.id = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") String groupId);

    int countByGroupId(String groupId);

    boolean existsByGroupIdAndUserId(String groupId, Long userId);

    @Modifying
    void deleteByGroupIdAndUserId(String groupId, Long userId);

    @Query("SELECT DISTINCT m.user FROM MonitorGroupMember m JOIN m.group g JOIN MonitorGroupUrl gu ON gu.group = g " +
            "WHERE gu.url.id = :urlId")
    List<User> findUsersByUrlId(@Param("urlId") String urlId);
}