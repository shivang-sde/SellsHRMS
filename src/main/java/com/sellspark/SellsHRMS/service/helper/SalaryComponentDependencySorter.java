package com.sellspark.SellsHRMS.service.helper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.sellspark.SellsHRMS.entity.payroll.SalaryComponent;

public class SalaryComponentDependencySorter {

    // Regex to extract potential component references like BASIC, HRA, PF, etc.
    private static final Pattern COMPONENT_REF_PATTERN = Pattern.compile("\\b[A-Z0-9_]+\\b");

    /**
     * Sort salary components based on their formula dependencies.
     * Components that depend on others will appear later in the list.
     */
    public static List<SalaryComponent> sortByDependencies(List<SalaryComponent> components) {
        if (components == null || components.isEmpty()) return Collections.emptyList();

        // Build lookup map for abbreviations → component
        Map<String, SalaryComponent> componentMap = components.stream()
                .collect(Collectors.toMap(SalaryComponent::getAbbreviation, c -> c));

        // Build dependency graph: component → set of abbreviations it depends on
        Map<String, Set<String>> dependencyGraph = new HashMap<>();

        for (SalaryComponent comp : components) {
            Set<String> dependencies = extractDependencies(comp.getFormula(), componentMap.keySet());
            dependencyGraph.put(comp.getAbbreviation(), dependencies);
        }

        // Perform topological sort
        List<String> sortedKeys = topologicalSort(dependencyGraph);

        // Convert back to component objects (maintaining sorted order)
        return sortedKeys.stream()
                .map(componentMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ────────────────────────────────────────────────────────────────
    // Extracts all valid component references from formula text
    private static Set<String> extractDependencies(String formula, Set<String> validKeys) {
        Set<String> deps = new HashSet<>();
        if (formula == null || formula.isBlank()) return deps;

        Matcher matcher = COMPONENT_REF_PATTERN.matcher(formula);
        while (matcher.find()) {
            String token = matcher.group();
            // Only count as dependency if it matches an existing component abbreviation
            if (validKeys.contains(token)) {
                deps.add(token);
            }
        }
        return deps;
    }

    // ────────────────────────────────────────────────────────────────
    // Standard topological sort (Kahn’s algorithm)
    private static List<String> topologicalSort(Map<String, Set<String>> graph) {
        Map<String, Set<String>> localGraph = new HashMap<>();
        graph.forEach((k, v) -> localGraph.put(k, new HashSet<>(v)));

        List<String> sorted = new ArrayList<>();
        Queue<String> ready = new ArrayDeque<>();

        // Find nodes with no dependencies
        localGraph.forEach((node, deps) -> {
            if (deps.isEmpty()) ready.add(node);
        });

        while (!ready.isEmpty()) {
            String node = ready.poll();
            sorted.add(node);

            // Remove current node from all others' dependency sets
            for (Set<String> deps : localGraph.values()) {
                deps.remove(node);
            }

            // Add new nodes that are now dependency-free
            localGraph.entrySet().removeIf(entry -> {
                if (entry.getValue().isEmpty() && !sorted.contains(entry.getKey()) && !ready.contains(entry.getKey())) {
                    ready.add(entry.getKey());
                }
                return false;
            });
        }

        // Detect circular dependencies
        if (sorted.size() < graph.size()) {
            System.err.println("⚠️ Circular dependency detected in salary component formulas!");
            // Optionally, fallback to unsorted to avoid crash
            sorted.addAll(graph.keySet().stream().filter(k -> !sorted.contains(k)).toList());
        }

        return sorted;
    }
}

