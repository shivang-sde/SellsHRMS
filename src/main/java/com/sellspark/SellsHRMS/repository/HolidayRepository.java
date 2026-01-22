package com.sellspark.SellsHRMS.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sellspark.SellsHRMS.entity.Holiday;
import com.sellspark.SellsHRMS.entity.Organisation;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    
    List<Holiday> findByOrganisationId(Long organisationId);

    List<Holiday> findByOrganisation(Organisation organisation);
    
    List<Holiday> findByOrganisationIdAndHolidayDateBetween(
        Long organisationId,
        LocalDate startDate,
        LocalDate endDate
    );
    
     List<Holiday> findByOrganisationAndHolidayDateBetween(
        Organisation organisation, LocalDate start, LocalDate end);

      Optional<Holiday> findByOrganisationIdAndHolidayDate(
        Long organisationId,
        LocalDate date
    );
    
    boolean existsByOrganisationIdAndHolidayDate(
        Long organisationId,
        LocalDate date
    );

     boolean existsByOrganisationAndHolidayDate(Organisation organisation, LocalDate holidayDate);
    
  
}