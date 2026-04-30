package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.device.DeviceDTO;
import com.sellspark.SellsHRMS.entity.Device;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.DeviceRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public Device createDevice(Long organisationId, String name, String deviceCode) {
        Organisation organisation = organisationRepository.findById(organisationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organisation", "id", organisationId));

        if (deviceRepository.existsByOrganisationIdAndDeviceCode(organisationId, deviceCode)) {
            throw new InvalidOperationException(
                    "Device with code " + deviceCode + " already exists in this organisation");
        }

        String apiKey = UUID.randomUUID().toString();

        Device device = Device.builder()
                .organisation(organisation)
                .name(name)
                .deviceCode(deviceCode)
                .apiKey(apiKey)
                .status(Device.Status.ACTIVE)
                .build();

        return deviceRepository.save(device);
    }

    @Override
    public DeviceDTO updateDeviceStatus(Long deviceId, Device.Status status) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", deviceId));

        device.setStatus(status);
        Device updatedDevice = deviceRepository.save(device);

        return mapToDTO(updatedDevice);
    }

    @Override
    public Device getDeviceByApiKey(String apiKey) {
        Device device = deviceRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new InvalidOperationException("Invalid API Key"));

        // Check if device is active
        if (device.getStatus() != Device.Status.ACTIVE) {
            throw new InvalidOperationException("Device is not active. Please contact administrator.");
        }

        return device;
    }

    @Override
    public List<DeviceDTO> getDevicesByOrganisation(Long organisationId) {
        return deviceRepository.findByOrganisationId(organisationId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private DeviceDTO mapToDTO(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .name(device.getName())
                .deviceCode(device.getDeviceCode())
                .apiKey(device.getApiKey())
                .status(device.getStatus().name())
                .createdAt(device.getCreatedAt())
                .build();
    }
}
