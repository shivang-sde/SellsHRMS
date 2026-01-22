package com.sellspark.SellsHRMS.repository.payroll;

import com.sellspark.SellsHRMS.entity.payroll.SalarySlipTemplate;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SalarySlipTemplateRepository extends JpaRepository<SalarySlipTemplate, Long> {

    // Find all templates for an organisation
    List<SalarySlipTemplate> findByOrganisation_IdAndIsActiveTrue(Long orgId);

    // Find default template for an organisation
    Optional<SalarySlipTemplate> findByOrganisation_IdAndIsDefaultTrueAndIsActiveTrue(Long orgId);

    // Find by ID and orgId (for security - ensure user can only access their org's templates)
    Optional<SalarySlipTemplate> findByIdAndOrganisation_Id(Long id, Long orgId);

    // Check if template name already exists for organisation
    boolean existsByOrganisation_IdAndTemplateNameAndIsActiveTrue(Long orgId, String templateName);

    // Unset all default templates for an organisation (before setting a new one)
    @Transactional
    @Modifying
    @Query("UPDATE SalarySlipTemplate s SET s.isDefault = false WHERE s.organisation.id = :orgId")
    void unsetDefaultForOrganisation(Long orgId);

    // Count templates for an organisation
    long countByOrganisation_IdAndIsActiveTrue(Long orgId);
}