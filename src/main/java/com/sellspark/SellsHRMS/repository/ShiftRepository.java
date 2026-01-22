package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByOrganisationId(Long organisationId);
}