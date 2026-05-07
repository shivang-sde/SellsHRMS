package com.sellspark.SellsHRMS.monitoring.service;

public interface MonitorService {

    /**
     * Run checks for all organisations - called by scheduler
     * Fetches URLs due for check and processes them in batches
     */
    void runChecks();

    /**
     * Clean up old check records - called by scheduler daily
     */
    void cleanupOldChecks();
}