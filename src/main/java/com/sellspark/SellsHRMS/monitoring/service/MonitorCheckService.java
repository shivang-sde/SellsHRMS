package com.sellspark.SellsHRMS.monitoring.service;

import com.sellspark.SellsHRMS.monitoring.dto.HttpCheckResult;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorIncident;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl.Status;

public interface MonitorCheckService {

    /**
     * Process a single URL check
     */
    void processCheck(MonitorUrl url);

    /**
     * Handle state changes and send notifications
     */
    void handleStateChangeAndNotify(MonitorUrl url, Status prevStatus, Status newStatus, HttpCheckResult result);

    void notifySuperAdminsForUrlDown(MonitorUrl url, MonitorIncident incident);
}
