package com.sellspark.SellsHRMS.repository;

import java.time.*;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import com.sellspark.SellsHRMS.entity.*;

public interface OrganisationEventRepository extends JpaRepository<OrganisationEvent, Long> {
    List<OrganisationEvent> findByOrganisationIdAndStartDateBetween(Long orgId, LocalDate start, LocalDate end);


     List<OrganisationEvent> findByOrganisation(Organisation organisation);
    List<OrganisationEvent> findByOrganisationOrderByStartDateDesc(Organisation organisation);
    List<OrganisationEvent> findByOrganisationAndStartDateGreaterThanEqualOrderByStartDateAsc(
        Organisation organisation, LocalDate date);
    List<OrganisationEvent> findByOrganisationAndTypeOrderByStartDateDesc(
        Organisation organisation, OrganisationEvent.EventType type);
}




 