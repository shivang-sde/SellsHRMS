package com.sellspark.SellsHRMS.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceRecoveryScheduler {

    private final AttendanceScheduler attendanceScheduler;

    @EventListener(ApplicationReadyEvent.class)
    public void recoverMissedAttendanceJobs() {

        log.info("========== ATTENDANCE RECOVERY START ==========");

        LocalTime now = LocalTime.now();

        try {

            /*
             * PREMARK should have run after 1:48 AM
             */
            if (now.isAfter(LocalTime.of(1, 48))) {
                log.info("Recovering missed PRE-MARK job...");
                attendanceScheduler.preMarkDailyAttendance();
            }

            /*
             * RECONCILE should run after 11:50 PM
             */
            if (now.isAfter(LocalTime.of(23, 50))) {
                log.info("Recovering missed RECONCILE job...");
                attendanceScheduler.reconcileDailyAttendance();
            }

        } catch (Exception e) {
            log.error("Recovery failed: {}", e.getMessage(), e);
        }

        log.info("========== ATTENDANCE RECOVERY END ==========");
    }
}