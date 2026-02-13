package com.sellspark.SellsHRMS.service.impl.payroll;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.payroll.*;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryComponentMappingRepository;
import com.sellspark.SellsHRMS.repository.payroll.StatutoryRuleRepository;
import com.sellspark.SellsHRMS.service.payroll.StatutoryComputationEngineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatutoryComputationEngineImpl implements StatutoryComputationEngineService {

        private final StatutoryRuleRepository ruleRepository;
        private final StatutoryComponentMappingRepository mappingRepository;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public record StatutoryResult(double employeeDeduction, double employerContribution) {
        }

        @Override
        public Map<String, StatutoryResult> computeDetailed(EmployeeSalaryAssignment assignment,
                        Map<SalaryComponent, Double> componentValues,
                        Organisation organisation) {
                log.info("🔍 Analyzing Statutory for Employee: {}", assignment.getEmployee().getEmployeeCode());
                Map<String, StatutoryResult> result = new LinkedHashMap<>();

                // 1. Fetch all active statutory mappings for this org
                List<StatutoryComponentMapping> mappings = mappingRepository
                                .findByOrganisationIdAndActiveTrue(organisation.getId());

                // 2. Group mappings by the Statutory Component ID (e.g., PF id=1)
                // This ensures we only calculate PF once even if it has multiple component
                // mappings
                Map<Long, List<StatutoryComponentMapping>> groupedMappings = mappings.stream()
                                .collect(Collectors.groupingBy(m -> m.getStatutoryComponent().getId()));

                for (var entry : groupedMappings.entrySet()) {
                        List<StatutoryComponentMapping> group = entry.getValue();
                        // Get the statutory metadata from the first mapping in the group
                        StatutoryComponent statutory = group.get(0).getStatutoryComponent();

                        // 3. DEFAULT LOGIC: Start with Base Pay from the assignment
                        double totalBase = (assignment != null && assignment.getBasePay() != null)
                                        ? assignment.getBasePay()
                                        : 0.0;

                        // 4. MAPPING LOGIC: Add values of all explicitly mapped components (HRA, DA,
                        // etc.)
                        for (StatutoryComponentMapping mapping : group) {
                                if (mapping.getSalaryComponent() != null) {
                                        totalBase += componentValues.getOrDefault(mapping.getSalaryComponent(), 0.0);
                                }
                        }

                        log.info("🔹 Statutory [{}] Consolidated Total Base: {}", statutory.getCode(), totalBase);

                        // Skip if there are no earnings to calculate against
                        if (totalBase <= 0)
                                continue;

                        // 5. Fetch rules for this specific statutory component
                        List<StatutoryRule> rules = ruleRepository.findByStatutoryComponentId(statutory.getId());
                        if (rules.isEmpty())
                                continue;
                        StatutoryRule rule = getEffectiveRule(rules, LocalDate.now());

                        // 6. Apply Statutory Limits
                        double minSal = rule.getMinApplicableSalary() != null ? rule.getMinApplicableSalary() : 0;
                        double maxSal = rule.getMaxApplicableSalary() != null ? rule.getMaxApplicableSalary()
                                        : totalBase;

                        double effectiveBase = totalBase;
                        if (totalBase < minSal) {
                                log.warn("⚠️ Base {} is below threshold {}. Setting to 0.", totalBase, minSal);
                                effectiveBase = 0;
                        } else {
                                effectiveBase = Math.min(totalBase, maxSal);
                        }

                        // 7. Compute Contributions using percentage from mapping (if overridden) or
                        // rule
                        // We take the first mapping's percent as the primary override for the group
                        double empPercent = Optional.ofNullable(group.get(0).getEmployeePercent())
                                        .orElse(rule.getEmployeeContributionPercent());
                        double orgPercent = Optional.ofNullable(group.get(0).getEmployerPercent())
                                        .orElse(rule.getEmployerContributionPercent());

                        double empContribution = effectiveBase * (empPercent / 100);
                        double orgContribution = effectiveBase * (orgPercent / 100);

                        result.put(statutory.getCode(), new StatutoryResult(
                                        roundToTwoDecimal(empContribution),
                                        roundToTwoDecimal(orgContribution)));

                        log.info("✅ Statutory [{}] Result -> Emp: {} | Org: {}",
                                        statutory.getCode(), empContribution, orgContribution);
                }

                return result;
        }

        private StatutoryRule getEffectiveRule(List<StatutoryRule> rules, LocalDate processingDate) {
                return rules.stream()
                                .filter(r -> (r.getEffectiveFrom() == null
                                                || !processingDate.isBefore(r.getEffectiveFrom())) &&
                                                (r.getEffectiveTo() == null
                                                                || !processingDate.isAfter(r.getEffectiveTo())))
                                .findFirst()
                                .orElse(rules.get(0));
        }

        private double roundToTwoDecimal(double val) {
                return Math.round(val * 100.0) / 100.0;
        }
}
