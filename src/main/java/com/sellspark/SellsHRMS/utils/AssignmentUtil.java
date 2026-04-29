package com.sellspark.SellsHRMS.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.sellspark.SellsHRMS.entity.Employee;

public class AssignmentUtil {

    public static record AssignmentDiff(
            Set<Long> oldIds,
            Set<Long> newIds,
            Set<Long> addedIds,
            Set<Long> removedIds,
            Set<Long> unchangedIds) {
    }

    public static AssignmentDiff compare(
            Collection<Employee> oldUsers,
            Collection<Long> newIdsInput) {

        Set<Long> oldIds = oldUsers.stream()
                .map(Employee::getId)
                .collect(Collectors.toSet());

        Set<Long> newIds = new HashSet<>(newIdsInput);

        Set<Long> added = new HashSet<>(newIds);
        added.removeAll(oldIds);

        Set<Long> removed = new HashSet<>(oldIds);
        removed.removeAll(newIds);

        Set<Long> unchanged = new HashSet<>(oldIds);
        unchanged.retainAll(newIds);

        return new AssignmentDiff(
                oldIds,
                newIds,
                added,
                removed,
                unchanged);
    }
}