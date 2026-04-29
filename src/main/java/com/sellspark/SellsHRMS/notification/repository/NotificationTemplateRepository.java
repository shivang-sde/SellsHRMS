package com.sellspark.SellsHRMS.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sellspark.SellsHRMS.notification.entity.NotificationTemplate;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

        Optional<NotificationTemplate> findByEventCodeAndTargetRoleAndIsActiveTrue(
                        String eventCode,
                        TargetRole targetRole);

        Optional<NotificationTemplate> findByEventCodeAndTargetRole(String eventCode, TargetRole targetRole);

        boolean existsByEventCodeAndTargetRole(String eventCode, TargetRole targetRole);

        List<NotificationTemplate> findAllByOrderByIdDesc();
}
