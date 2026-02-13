package com.sellspark.SellsHRMS.specification;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.Department;
import com.sellspark.SellsHRMS.entity.Designation;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class SalarySlipSpecification {

    public static Specification<SalarySlip> buildSpecification(Long orgId, Integer month, Integer year,
            Long departmentId, Boolean isCredited, String search) {

        /*
         * ---------- NOTES ----------
         * 1. root: represents the SalarySlip entity
         * 2. query: represents the query
         * 3. cb: represents the criteria builder, used to build predicates,
         * expressions, and orderings
         * 4. Predicate: represents a condition that can be used to filter entities
         * 5. Join: represents a join between two entities
         * 6. Expression: represents an expression that can be used to filter entities
         * 7. Ordering: represents an ordering that can be used to sort entities
         */
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction(); // start with true.

            // 🔹 Filter by Organisation
            if (orgId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("organisation").get("id"), orgId));
            }

            // 🔹 Filter by PayRun Month & Year
            if (month != null && year != null) {
                Predicate payRunPredicate = cb.and(
                        cb.equal(root.get("payRun").get("month"), month),
                        cb.equal(root.get("payRun").get("year"), year));
                predicate = cb.and(predicate, payRunPredicate);
            }

            // 🔹 Filter by Department
            if (departmentId != null) {
                Join<SalarySlip, Employee> empJoin = root.join("employee");
                Join<Employee, Department> deptJoin = empJoin.join("department");
                predicate = cb.and(predicate, cb.equal(deptJoin.get("id"), departmentId));
            }

            // 🔹 Filter by Credited Status
            if (isCredited != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isCredited"), isCredited));
            }

            // 🔹 Search: Employee Name, Code, Department, Designation
            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";

                Join<SalarySlip, Employee> empJoin = root.join("employee");
                Join<Employee, Department> deptJoin = empJoin.join("department", JoinType.LEFT);
                Join<Employee, Designation> desJoin = empJoin.join("designation", JoinType.LEFT);

                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(empJoin.get("firstName")), like),
                        cb.like(cb.lower(empJoin.get("lastName")), like),
                        cb.like(cb.lower(empJoin.get("employeeCode")), like),
                        cb.like(cb.lower(deptJoin.get("name")), like),
                        cb.like(cb.lower(desJoin.get("title")), like));

                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };
    }
}
