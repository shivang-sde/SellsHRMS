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

    @Override
    public Map<String, Double> compute(Employee employee, Map<SalaryComponent, Double> componentValues, Organisation organisation) {
        Map<String, Double> result = new LinkedHashMap<>();

        // Fetch all active statutory mappings for this org
        List<StatutoryComponentMapping> mappings = mappingRepository.findByOrganisationIdAndActiveTrue(organisation.getId());

        for (StatutoryComponentMapping mapping : mappings) {
            StatutoryComponent statutory = mapping.getStatutoryComponent();

            

            // Find active rules for this component & location
            List<StatutoryRule> rules = ruleRepository.findByStatutoryComponentId(statutory.getId());

            if (rules.isEmpty()) continue;

            // Filter salary components this statutory applies to
            List<SalaryComponent> applicableComponents = mappings.stream()
                    .filter(m -> Objects.equals(m.getStatutoryComponent().getId(), statutory.getId()))
                    .map(StatutoryComponentMapping::getSalaryComponent)
                    .collect(Collectors.toList());

            // Compute total base for statutory
            double totalBase = applicableComponents.stream()
                    .mapToDouble(c -> componentValues.getOrDefault(c, 0.0))
                    .sum();

                log.info("total abse statutory rule {}", totalBase);

            if (totalBase <= 0) continue;

            StatutoryRule rule = getEffectiveRule(rules);

            // Apply min/max limits
            double effectiveBase = Math.min(Math.max(totalBase, rule.getMinApplicableSalary() != null ? rule.getMinApplicableSalary() : 0),
                    rule.getMaxApplicableSalary() != null ? rule.getMaxApplicableSalary() : totalBase);

            // Compute contributions
            double empPercent = Optional.ofNullable(mapping.getEmployeePercent()).orElse(rule.getEmployeeContributionPercent());
            double orgPercent = Optional.ofNullable(mapping.getEmployerPercent()).orElse(rule.getEmployerContributionPercent());

            double empContribution = effectiveBase * (empPercent / 100);
            double orgContribution = effectiveBase * (orgPercent / 100);


            

            // Optional pro-rata adjustment,  for paymentdays and working days you have to look for attendance summary thing.
            // if (Boolean.TRUE.equals(rule.getApplyProRata()) && employee.getWorkingDays() != null && employee.getPaymentDays() != null) {
            //     double ratio = (double) employee.getPaymentDays() / employee.getWorkingDays();
            //     empContribution *= ratio;
            //     orgContribution *= ratio;
            // }

            double totalDeduction = empContribution; // for now employee side deducted from pay
            result.put(statutory.getCode(), roundToTwoDecimal(totalDeduction));

            log.info("statutory code {} -> empPercent {}, orgPercent {}, empContri {}, org Contri {}", statutory.getCode(), empPercent, orgPercent, empContribution, orgContribution);

            // Optional logging for audit (if additional_config JSON present)
            if (rule.getAdditionalConfig() != null) {
                try {
                    JsonNode cfg = objectMapper.readTree(rule.getAdditionalConfig());
                    // Example: handle country-specific exemptions or max caps
                } catch (Exception ignored) {}
            }
        }

        return result;
    }

    private StatutoryRule getEffectiveRule(List<StatutoryRule> rules) {
        LocalDate today = LocalDate.now();
        return rules.stream()
                .filter(r -> (r.getEffectiveFrom() == null || !today.isBefore(r.getEffectiveFrom())) &&
                             (r.getEffectiveTo() == null || !today.isAfter(r.getEffectiveTo())))
                .findFirst()
                .orElse(rules.get(0));
    }

    private double roundToTwoDecimal(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}

