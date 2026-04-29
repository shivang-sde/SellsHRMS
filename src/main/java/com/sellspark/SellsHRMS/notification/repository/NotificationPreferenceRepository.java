package com.sellspark.SellsHRMS.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.notification.entity.NotificationPreference;
import com.sellspark.SellsHRMS.notification.enums.TargetRole;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

        Optional<NotificationPreference> findByOrganisation_IdAndNotificationEvent_EventCodeAndEmailEnabledTrue(
                        Long orgId,
                        String eventCode);

        List<NotificationPreference> findByOrganisationId(Long orgId);

        List<NotificationPreference> findByOrganisationIdAndEmailEnabledTrue(Long orgId);

        Optional<NotificationPreference> findByOrganisationIdAndNotificationEventId(Long orgId, Long eventId);

        boolean existsByOrganisationIdAndNotificationEventId(Long orgId, Long eventId);

}
