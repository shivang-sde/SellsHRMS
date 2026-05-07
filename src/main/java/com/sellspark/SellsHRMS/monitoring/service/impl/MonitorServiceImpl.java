package com.sellspark.SellsHRMS.monitoring.service.impl;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.monitoring.entity.MonitorUrl;
import com.sellspark.SellsHRMS.monitoring.repository.MonitorCheckRepository;
import com.sellspark.SellsHRMS.monitoring.repository.MonitorUrlRepository;
import com.sellspark.SellsHRMS.monitoring.service.MonitorCheckService;
import com.sellspark.SellsHRMS.monitoring.service.MonitorService;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final OrganisationRepository organisationRepository;
    private final MonitorUrlRepository urlRepository;
    private final MonitorCheckService monitorCheckService;
    private final MonitorCheckRepository checkRepository;

    @Override
    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    @Transactional
    public void runChecks() {
        log.debug("Running URL monitor checks...");

        List<Organisation> organisations = organisationRepository.findByIsActiveTrue();

        for (Organisation org : organisations) {
            try {
                Pageable limit = PageRequest.of(0, 20);
                List<MonitorUrl> urls = urlRepository.findUrlsDueForCheck(org.getId(), limit).getContent();

                if (urls.isEmpty()) {
                    continue;
                }

                log.info("Processing {} URLs for organisation: {}", urls.size(), org.getName());

                // Process in parallel with max 5 concurrent threads
                ExecutorService executor = Executors.newFixedThreadPool(5);
                List<CompletableFuture<Void>> futures = urls.stream()
                        .map(url -> CompletableFuture.runAsync(() -> {
                            try {
                                monitorCheckService.processCheck(url);
                            } catch (Exception e) {
                                log.error("Error processing URL check: {}", url.getId(), e);
                            }
                        }, executor))
                        .collect(Collectors.toList());

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                executor.shutdown();

            } catch (Exception e) {
                log.error("Error processing checks for org: {}", org.getId(), e);
            }
        }
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?") // Run daily at 3 AM
    @Transactional
    public void cleanupOldChecks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int deleted = checkRepository.deleteOldChecks(cutoff);
        log.info("Cleanup: {} old monitor checks removed", deleted);
    }
}