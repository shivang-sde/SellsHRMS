package com.sellspark.SellsHRMS.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);

    Optional<User> findByEmployeeId(Long employeeId);

    List<User> findByOrganisationId(Long organisationId);

    @Query("SELECT u FROM User u JOIN FETCH u.organisation WHERE u.email = :email")
    Optional<User> findByEmailWithOrganisation(@Param("email") String email);

    @Query("""
                SELECT DISTINCT m.user
                FROM MonitorGroupMember m
                JOIN m.group g
                JOIN MonitorGroupUrl gu ON gu.group = g
                WHERE gu.url.id = :urlId
            """)
    List<User> findUsersByUrlId(@Param("urlId") String urlId);

    // In UserRepository.java
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN MonitorGroupMember mgm ON mgm.user.id = u.id " +
            "JOIN MonitorGroupUrl mgu ON mgu.group.id = mgm.group.id " +
            "WHERE mgu.url.id = :urlId AND u.isActive = true")
    List<User> findByMonitorGroupUrlsUrlId(@Param("urlId") String urlId);

    /*
     * SELECT DISTINCT u.*
     * FROM monitor_group_members mgm
     * JOIN monitor_groups g ON mgm.group_id = g.id
     * JOIN monitor_group_urls gu ON gu.group_id = g.id
     * JOIN monitor_urls mu ON gu.url_id = mu.id
     * JOIN users u ON mgm.user_id = u.id
     * WHERE mu.id = :urlId;
     * 
     */

    @Query("SELECT u FROM User u WHERE u.systemRole = :role")
    List<User> findBySystemRole(@Param("role") User.SystemRole role);
}
