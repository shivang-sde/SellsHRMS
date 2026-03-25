package com.sellspark.SellsHRMS.validator;

import com.sellspark.SellsHRMS.dto.payroll.SalaryComponentDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;
import com.sellspark.SellsHRMS.utils.FormulaExpressionEvaluator;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SalaryFormulaValidator {

    private static final Pattern COMPONENT_REF_PATTERN = Pattern.compile("\\b[A-Za-z_][A-Za-z0-9_]*\\b");

    public void validateFormula(SalaryComponentDTO dto, List<SalaryComponent> currentOrgComponents) {
        if (!"FORMULA".equalsIgnoreCase(dto.getCalculationType()) && !"PERCENTAGE".equalsIgnoreCase(dto.getCalculationType())) {
            return;
        }

        String formula = dto.getFormula();
        if ("PERCENTAGE".equalsIgnoreCase(dto.getCalculationType())) {
             if (formula == null || formula.isBlank()) {
                 return; // PERCENTAGE can fallback to BASE * percent, so formula is optional
             }
        } else {
             if (formula == null || formula.isBlank()) {
                 throw new IllegalArgumentException("Formula cannot be empty for " + dto.getCalculationType() + " calculation type.");
             }
        }

        // 1. Gather existing component abbreviations
        Set<String> existingAbbreviations = currentOrgComponents.stream()
                .filter(c -> c.getActive() != null && c.getActive())
                .filter(c -> dto.getId() == null || !dto.getId().equals(c.getId()))
                .map(SalaryComponent::getAbbreviation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        Set<String> builtinKeys = new HashSet<>(Arrays.asList("BASE", "WORKING_DAYS", "PAYMENT_DAYS", "LOP_DAYS", "VARPAY", "COUNTRY", "ORG_ID", "DATE_NOW", "BASEPAY", "GROSS"));

        Set<String> validTokens = new HashSet<>(existingAbbreviations);
        validTokens.addAll(builtinKeys);

        // Extract referenced tokens
        Set<String> referencedTokens = new HashSet<>();
        Matcher matcher = COMPONENT_REF_PATTERN.matcher(formula);
        while (matcher.find()) {
            referencedTokens.add(matcher.group());
        }

        // 2. Validate referenced components exist
        List<String> missing = referencedTokens.stream()
                .filter(token -> !validTokens.contains(token) && !isNumeric(token) && !isKeyword(token) && !token.startsWith("COMP"))
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Formula references non-existent components: " + missing + ". Please create these components first or fix the formula.");
        }

        // 3. Syntax check with sample context
        Map<String, Object> sampleContext = new HashMap<>();
        for (String abbr : validTokens) {
            sampleContext.put(abbr, 1000.0);
            sampleContext.put("COMP:" + abbr, 1000.0); // For COMP:XXX references
        }
        boolean isValidSyntax = FormulaExpressionEvaluator.validateFormula(formula, sampleContext);
        if (!isValidSyntax) {
            throw new IllegalArgumentException("Invalid formula syntax: " + formula);
        }

        // 4. Circular Dependency Check
        List<SalaryComponent> mockComponentsForCycleCheck = new ArrayList<>(currentOrgComponents);
        // remove self if updating to avoid duplicate
        if (dto.getId() != null) {
            mockComponentsForCycleCheck.removeIf(c -> c.getId().equals(dto.getId()));
        }

        SalaryComponent mockComponent = new SalaryComponent();
        mockComponent.setAbbreviation(dto.getAbbreviation());
        mockComponent.setFormula(formula);
        mockComponent.setActive(true);
        mockComponentsForCycleCheck.add(mockComponent);

        detectCircularDependencies(mockComponentsForCycleCheck);
    }

    private void detectCircularDependencies(List<SalaryComponent> components) {
        Map<String, SalaryComponent> componentMap = components.stream()
                .filter(c -> c.getAbbreviation() != null)
                .collect(Collectors.toMap(SalaryComponent::getAbbreviation, c -> c, (a, b) -> a));

        Map<String, Set<String>> dependencyGraph = new HashMap<>();
        for (SalaryComponent comp : components) {
            if (comp.getAbbreviation() == null) continue;
            Set<String> deps = new HashSet<>();
            if (comp.getFormula() != null) {
                Matcher matcher = COMPONENT_REF_PATTERN.matcher(comp.getFormula());
                while (matcher.find()) {
                    String token = matcher.group();
                    if (componentMap.containsKey(token)) {
                        deps.add(token);
                    }
                }
            }
            dependencyGraph.put(comp.getAbbreviation(), deps);
        }

        Map<String, Set<String>> localGraph = new HashMap<>();
        dependencyGraph.forEach((k, v) -> localGraph.put(k, new HashSet<>(v)));

        List<String> sorted = new ArrayList<>();
        Queue<String> ready = new ArrayDeque<>();

        localGraph.forEach((node, deps) -> {
            if (deps.isEmpty()) ready.add(node);
        });

        while (!ready.isEmpty()) {
            String node = ready.poll();
            sorted.add(node);

            for (Set<String> deps : localGraph.values()) {
                deps.remove(node);
            }

            localGraph.entrySet().removeIf(entry -> {
                if (entry.getValue().isEmpty() && !sorted.contains(entry.getKey()) && !ready.contains(entry.getKey())) {
                    ready.add(entry.getKey());
                }
                return false;
            });
        }

        if (sorted.size() < dependencyGraph.size()) {
             List<String> cycleNodes = dependencyGraph.keySet().stream()
                     .filter(k -> !sorted.contains(k))
                     .collect(Collectors.toList());
             throw new IllegalArgumentException("Circular dependency detected involving components: " + cycleNodes);
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isKeyword(String str) {
        return Arrays.asList("COMP", "T", "F", "true", "false", "null", "AND", "OR", "NOT").contains(str);
    }
}
