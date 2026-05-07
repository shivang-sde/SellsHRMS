package com.sellspark.SellsHRMS.monitoring.repository;

import com.sellspark.SellsHRMS.monitoring.entity.MonitorGroup;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitorGroupRepository extends JpaRepository<MonitorGroup, String> {

    Page<MonitorGroup> findByOrganisationId(Long organisationId, Pageable pageable);

    List<MonitorGroup> findByOrganisationId(Long organisationId);

    boolean existsByOrganisationIdAndName(Long organisationId, String name);

    boolean existsByOrganisationIdAndNameAndIdNot(Long organisationId, String name, String id);

    long countByOrganisationId(Long organisationId);
}
