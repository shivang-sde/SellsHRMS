package com.sellspark.SellsHRMS.scheduler;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;
import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.repository.payroll.PayRunRepository;
import com.sellspark.SellsHRMS.service.payroll.PayrollCalculationService;
import com.sellspark.SellsHRMS.utils.FormulaExpressionEvaluator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayrollTestStartupRunner {

    private final OrganisationRepository organisationRepository;
    private final OrganisationPolicyRepository policyRepository;
    private final PayRunRepository payRunRepository;
    private final PayrollCalculationService payrollService;

    private final Long TEST_ORG_ID = 7L; // organisation to test

    @PostConstruct
    public void runPayrollForTestOrg() {
        LocalDate today = LocalDate.now();
        log.info("🕑 Running test payroll for org ID {} on startup (date: {})", TEST_ORG_ID, today);

        Optional<Organisation> orgOpt = organisationRepository.findById(TEST_ORG_ID);
        if (orgOpt.isEmpty()) {
            log.error("❌ Organisation with ID {} not found.", TEST_ORG_ID);
            return;
        }

        Organisation org = orgOpt.get();

        Optional<OrganisationPolicy> policyOpt = policyRepository.findByOrganisationId(TEST_ORG_ID);
        if (policyOpt.isEmpty()) {
            log.error("❌ OrganisationPolicy not found for org ID {}", TEST_ORG_ID);
            return;
        }

        OrganisationPolicy policy = policyOpt.get();

        LocalDate[] cycle = calculateCycleDates(today, policy);
        LocalDate cycleStart = cycle[0];
        LocalDate cycleEnd = cycle[1];

        // Delete previous PayRuns and related SalarySlips for this org & cycle
        log.info("🗑️ Deleting existing PayRuns for org {} [{} - {}]", org.getName(), cycleStart, cycleEnd);
        payRunRepository.findByOrganisation_IdAndStartDateAndEndDate(TEST_ORG_ID, cycleStart, cycleEnd)
                .ifPresent(payRun -> {
                    // payrollService.deletePayRun(payRun.getId()); // make sure your service can handle deleting slips
                    payRunRepository.delete(payRun);
                });

        // Create new PayRun
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
        log.info("▶️ Running payroll for org {} [{} - {}]", org.getName(), cycleStart, cycleEnd);

        // Compute salary slips
        payrollService.runPayroll(TEST_ORG_ID, payRun);

        // Mark completed
        payRun.setStatus(PayRun.PayRunStatus.COMPLETED);
        payRunRepository.save(payRun);
        log.info("✅ Test payroll completed for org {} [{} - {}]", org.getName(), cycleStart, cycleEnd);

        Map<String, Object> basectx = new HashMap<>();
        basectx.put("BASE", 30000.0);
        System.out.println(FormulaExpressionEvaluator.evaluate("BASE * 0.12", basectx));
        log.info("base context {}",  basectx);

    }

    private LocalDate[] calculateCycleDates(LocalDate today, OrganisationPolicy policy) {
        Integer startDay = Optional.ofNullable(policy.getSalaryCycleStartDay()).orElse(1);
        Integer duration = Optional.ofNullable(policy.getCycleDuration()).orElse(14);

        LocalDate cycleStart = findPreviousCycleStart(today, startDay);
        LocalDate cycleEnd = cycleStart.plusDays(duration - 1);
        return new LocalDate[]{cycleStart, cycleEnd};
    }

    private LocalDate findPreviousCycleStart(LocalDate today, int startDay) {
        if (today.getDayOfMonth() < startDay) {
            LocalDate prevMonth = today.minusMonths(1);
            startDay = Math.min(startDay, prevMonth.lengthOfMonth());
            return LocalDate.of(prevMonth.getYear(), prevMonth.getMonth(), startDay);
        } else {
            startDay = Math.min(startDay, today.lengthOfMonth());
            return LocalDate.of(today.getYear(), today.getMonth(), startDay);
        }
    }
}
