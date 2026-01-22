package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.PayRun;
import com.sellspark.SellsHRMS.entity.payroll.PayRun.PayRunStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayRunRepository extends JpaRepository<PayRun, Long> {

    // 🔹 All pay runs for an organisation
    List<PayRun> findByOrganisation_IdOrderByStartDateDesc(Long organisationId);

    PayRun findByOrganisation_IdAndMonthAndYear(Long organisationId, Integer month, Integer year);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM PayRun p
        WHERE p.organisation.id = :orgId
        AND (p.startDate <= :cycleEnd AND p.endDate >= :cycleStart)
    """)
    boolean existsOverlap(Long orgId, LocalDate cycleStart, LocalDate cycleEnd);

    // 🔹 Active/Ready pay runs
    List<PayRun> findByOrganisation_IdAndStatus(Long organisationId, PayRunStatus status);

    boolean existsByOrganisation_IdAndMonthAndYear(Long organisationId, Integer month, Integer year);

    // 🔹 Check if a pay run already exists for the same period
    Optional<PayRun> findByOrganisation_IdAndStartDateAndEndDate(Long organisationId, LocalDate startDate, LocalDate endDate);

     @EntityGraph(attributePaths = {"salarySlips", "salarySlips.employee", "salarySlips.components"})
    Optional<PayRun> findById(Long id);
}
