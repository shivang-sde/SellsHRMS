package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByApiKey(String apiKey);

    List<Device> findByOrganisationId(Long organisationId);

    boolean existsByOrganisationIdAndDeviceCode(Long organisationId, String deviceCode);
}
