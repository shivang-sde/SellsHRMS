package com.sellspark.SellsHRMS.monitoring.service;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorIncident;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;
import com.sellspark.SellsHRMS.notification.event.NotificationEventData;

public interface MonitorNotificationEventBuilder {

    /**
     * Build notification event for URL DOWN alert
     */
    NotificationEventData buildUrlDownEvent(MonitorUrl url, MonitorIncident incident);

    /**
     * Build notification event for URL RECOVERED alert
     */
    NotificationEventData buildUrlRecoveredEvent(MonitorUrl url, MonitorIncident incident);

    NotificationEventData buildSuperAdminUrlDownEvent(MonitorUrl url, MonitorIncident incident);

    /**
     * Send notifications to all group members for a URL
     */
    void sendToGroupMembers(MonitorUrl url, MonitorIncident incident, String eventCode);

}
