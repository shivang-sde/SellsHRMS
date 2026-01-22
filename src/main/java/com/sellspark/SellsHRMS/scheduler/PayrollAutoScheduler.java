package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayrollAutoScheduler {

    private final OrganisationRepository organisationRepository;
    private final OrganisationPolicyRepository policyRepository;
    private final PayRunRepository payRunRepository;
    private final PayrollCalculationService payrollService;

    /**
     * Runs daily at 2 AM and checks if today is payroll generation day
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void autoGeneratePayslips() {
        LocalDate today = LocalDate.now();
        log.info("🕑 Starting auto payroll scheduler for date: {}", today);

        List<Organisation> orgs = organisationRepository.findAll();
        for (Organisation org : orgs) {
            try {
                Optional<OrganisationPolicy> optPolicy = policyRepository.findByOrganisationId(org.getId());
                if (optPolicy.isEmpty()) continue;

                OrganisationPolicy policy = optPolicy.get();

                LocalDate[] cycle = calculateCycleDates(today, policy);
                LocalDate cycleStart = cycle[0];
                LocalDate cycleEnd = cycle[1];
                LocalDate generationDate = cycleEnd.plusDays(
                        Optional.ofNullable(policy.getPayslipGenerationOffsetDays()).orElse(0)
                );

                // Skip if not today
                if (!today.equals(generationDate)) {
                    continue;
                }

                // Prevent duplicate/overlapping PayRuns
                boolean exists = payRunRepository.existsOverlap(org.getId(), cycleStart, cycleEnd);
                if (exists) {
                    log.info("⏩ Skipping payroll for {}, overlap found ({} to {})", org.getName(), cycleStart, cycleEnd);
                    continue;
                }

                //Create PayRun
                PayRun payRun = PayRun.builder()
                        .organisation(org)
                        .startDate(cycleStart)
                        .endDate(cycleEnd)
                        .month(cycleEnd.getMonthValue())
                        .year(cycleEnd.getYear())
                        .periodLabel(cycleEnd.getMonth().name() + " " + cycleEnd.getYear())
                        .status(PayRun.PayRunStatus.PROCESSING)
                        .runDate(today)
                        .build();
                payRun = payRunRepository.save(payRun);

                log.info("▶️ Running payroll for org {} for {} to {}", org.getName(), cycleStart, cycleEnd);

                // Compute salary slips
                payrollService.runPayroll(org.getId(), payRun);

                // Mark completed
                payRun.setStatus(PayRun.PayRunStatus.COMPLETED);
                payRunRepository.save(payRun);

                log.info("✅ Payroll completed for {} [{} - {}]", org.getName(), cycleStart, cycleEnd);

            } catch (Exception ex) {
                log.error("❌ Error while processing payroll for org {}", org.getName(), ex);
            }
        }
    }

    /**
     * Decides payroll cycle based on organisation policy.
     * Supports both fixed and variable duration cycles.
     */
    private LocalDate[] calculateCycleDates(LocalDate today, OrganisationPolicy policy) {
        Integer startDay = Optional.ofNullable(policy.getSalaryCycleStartDay()).orElse(1);
        Integer duration = Optional.ofNullable(policy.getCycleDuration()).orElse(30);

        // Calculate cycle start
        LocalDate cycleStart = findPreviousCycleStart(today, startDay);
        LocalDate cycleEnd = cycleStart.plusDays(duration - 1);

        return new LocalDate[]{cycleStart, cycleEnd};
    }

    /**
     * Finds the start date of the current cycle.
     * Example: startDay = 26, today = 5 Feb → start = 26 Jan
     */
    private LocalDate findPreviousCycleStart(LocalDate today, int startDay) {
        if (today.getDayOfMonth() < startDay) {
            // Start in previous month
            LocalDate prevMonth = today.minusMonths(1);
            int maxDay = prevMonth.lengthOfMonth();
            startDay = Math.min(startDay, maxDay); // handle Feb case
            return LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), startDay);
        } else {
            int maxDay = today.lengthOfMonth();
            startDay = Math.min(startDay, maxDay);
            return LocalDate.of(today.getYear(), today.getMonth(), startDay);
        }
    }
}
