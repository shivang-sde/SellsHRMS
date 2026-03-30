package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
     * How many past cycles to look back and catch up if missed.
     * 3 means: current cycle + 2 previous months.
     */
    private static final int CATCHUP_CYCLES = 3;

    @Scheduled(cron = "0 34 03 * * *", zone = "Asia/Kolkata")
    public void autoGeneratePayslips() {
        LocalDate today = LocalDate.now();
        log.info("🕑 Starting auto payroll scheduler for date: {}", today);

        List<Organisation> orgs = organisationRepository.findAll();

        for (Organisation org : orgs) {
            try {
                Optional<OrganisationPolicy> optPolicy = policyRepository.findByOrganisationId(org.getId());
                if (optPolicy.isEmpty()) {
                    log.warn("⚠️ No policy found for org {}", org.getName());
                    continue;
                }

                OrganisationPolicy policy = optPolicy.get();

                // Build list of cycles to check: current + previous N-1
                List<LocalDate[]> cyclesToCheck = buildCyclesToCheck(today, policy, org);

                for (LocalDate[] cycle : cyclesToCheck) {
                    processCycle(org, policy, cycle[0], cycle[1], today);
                }

            } catch (Exception ex) {
                log.error("❌ Error while processing payroll for org {}", org.getName(), ex);
            }
        }
    }

    /**
     * Attempts to process a single cycle for a given org.
     * Skips if not yet due, or if already processed.
     */
    private void processCycle(Organisation org,
            OrganisationPolicy policy,
            LocalDate cycleStart,
            LocalDate cycleEnd,
            LocalDate today) {

        int offsetDays = Optional.ofNullable(policy.getPayslipGenerationOffsetDays()).orElse(0);
        LocalDate generationDate = cycleEnd.plusDays(offsetDays);

        // Not yet due for this cycle — skip
        if (today.isBefore(generationDate)) {
            log.debug("⏭ Skipping {} [{} to {}] — not yet due (generationDate={})",
                    org.getName(), cycleStart, cycleEnd, generationDate);
            return;
        }

        // Already processed — skip (this handles duplicates AND the normal flow)
        boolean exists = payRunRepository.existsSuccessfulOrActiveOverlap(org.getId(), cycleStart, cycleEnd);
        if (exists) {
            log.debug("⏩ Skipping {} [{} to {}] — PayRun already exists",
                    org.getName(), cycleStart, cycleEnd);
            return;
        }

        // This is either today's scheduled run OR a missed one — process it
        boolean isCatchUp = today.isAfter(generationDate);
        log.info("{} payroll for org {} [{} to {}]",
                isCatchUp ? "🔁 Catching up missed" : "▶️ Running scheduled",
                org.getName(), cycleStart, cycleEnd);

        PayRun payRun = PayRun.builder()
                .organisation(org)
                .startDate(cycleStart)
                .endDate(cycleEnd)
                .month(cycleStart.getMonthValue())
                .year(cycleStart.getYear())
                .periodLabel(cycleStart.getMonth().name() + " " + cycleStart.getYear())
                .status(PayRun.PayRunStatus.PROCESSING)
                .runDate(today)
                .build();

        payRun = payRunRepository.save(payRun);

        try {
            payrollService.runPayroll(org.getId(), payRun);
            payRun.setStatus(PayRun.PayRunStatus.COMPLETED);
            log.info("✅ Payroll completed for {} [{} to {}]", org.getName(), cycleStart, cycleEnd);
        } catch (Exception ex) {
            payRun.setStatus(PayRun.PayRunStatus.FAILED);
            log.error("❌ Payroll FAILED for {} [{} to {}]", org.getName(), cycleStart, cycleEnd, ex);
        } finally {
            payRunRepository.save(payRun);
        }
    }

    /**
     * Builds a list of [cycleStart, cycleEnd] pairs to check.
     * Starts from CATCHUP_CYCLES months back and works up to the current cycle.
     *
     * Example with CATCHUP_CYCLES=3 and today=May 15, startDay=26:
     * → [Mar 26 – Apr 25], [Apr 26 – May 25], [May 26 – Jun 25] (only first two are
     * due)
     */
    private List<LocalDate[]> buildCyclesToCheck(LocalDate today,
            OrganisationPolicy policy,
            Organisation org) {
        List<LocalDate[]> cycles = new ArrayList<>();

        LocalDate orgBoundary = Optional.ofNullable(org.getCreatedAt())
                .map(LocalDateTime::toLocalDate)
                .orElse(today);

        LocalDate[] currentCycle = calculateCycleDates(today, policy);
        LocalDate currentCycleStart = currentCycle[0];

        for (int i = CATCHUP_CYCLES - 1; i >= 0; i--) {
            LocalDate anchorDate = currentCycleStart.minusMonths(i);
            LocalDate[] cycle = calculateCycleDates(anchorDate, policy);

            LocalDate cycleStart = cycle[0];
            LocalDate cycleEnd = cycle[1];

            // Rule 1: Entire cycle ended before org existed — skip completely
            if (cycleEnd.isBefore(orgBoundary)) {
                log.info("⏭ Skipping cycle [{} to {}] for org '{}' — cycle ended before org creation ({})",
                        cycleStart, cycleEnd, org.getName(), orgBoundary);
                continue;
            }

            // Rule 2: Cycle started before org was created but ends after —
            // this is a partial first cycle (e.g. org created Mar 10, cycle Feb 25–Mar 24)
            // Adjust the cycle start to org creation date so we don't imply
            // payroll responsibility before they existed.
            // Attendance records won't exist before orgBoundary anyway,
            // but we log it clearly for traceability.
            if (cycleStart.isBefore(orgBoundary)) {
                log.info("📅 Org '{}' created mid-cycle. Effective start adjusted: [{} → {}] (cycle end: {})",
                        org.getName(), cycleStart, orgBoundary, cycleEnd);
                // Replace cycleStart with orgBoundary for this cycle only
                cycle[0] = orgBoundary;
            }

            cycles.add(cycle);
        }

        return cycles;

        /*
         * ```
         * ---
         * ###
         * 
         * Why adjusting`cycle[0]` is safe
         * The adjusted`cycleStart`
         * flows into`processCycle`→`PayRun.startDate`→`calculateAttendanceStats(from,
         * to)`. Since attendance records only exist from the org's first active day
         * anyway, the query result is the same — but now the `PayRun` record in your DB
         * accurately reflects when the org actually started, not a date before they
         * even existed on your platform.
         * ```
         * Org created: Mar 10
         * Cycle (raw): Feb 25 – Mar 24
         * Cycle (adjusted): Mar 10 – Mar 24 ← saved in tbl_pay_run
         * Attendance query: Mar 10 – Mar 24 ← correct window
         * Pro-ration: totalCycleDays = DAYS(Feb 25, Mar 24) + 1 = 28
         * paymentDays from attendance (Mar 10–24 only)
         * salary = basePay * (paymentDays / 28) ✅
         */
    }

    /**
     * Given any date that falls within a cycle, returns [cycleStart, cycleEnd].
     */
    private LocalDate[] calculateCycleDates(LocalDate referenceDate, OrganisationPolicy policy) {
        int startDay = Optional.ofNullable(policy.getSalaryCycleStartDay()).orElse(1);

        LocalDate cycleStart;
        if (referenceDate.getDayOfMonth() < startDay) {
            LocalDate prevMonth = referenceDate.minusMonths(1);
            int validStartDay = Math.min(startDay, prevMonth.lengthOfMonth());
            cycleStart = LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), validStartDay);
        } else {
            int validStartDay = Math.min(startDay, referenceDate.lengthOfMonth());
            cycleStart = LocalDate.of(referenceDate.getYear(), referenceDate.getMonth(), validStartDay);
        }

        LocalDate nextMonth = cycleStart.plusMonths(1);
        int validNextStartDay = Math.min(startDay, nextMonth.lengthOfMonth());
        LocalDate nextCycleStart = LocalDate.of(nextMonth.getYear(), nextMonth.getMonth(), validNextStartDay);
        LocalDate cycleEnd = nextCycleStart.minusDays(1);

        return new LocalDate[] { cycleStart, cycleEnd };
    }
}