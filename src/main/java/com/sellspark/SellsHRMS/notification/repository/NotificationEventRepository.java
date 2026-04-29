package com.sellspark.SellsHRMS.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.notification.entity.NotificationEvent;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {

    List<NotificationEvent> findByModule(String module);

    List<NotificationEvent> findByIsActiveTrueOrderByModuleAsc();

    Optional<NotificationEvent> findByEventCodeAndIsActiveTrue(String eventCode);

    Optional<NotificationEvent> findByEventCode(String eventCode);
}
