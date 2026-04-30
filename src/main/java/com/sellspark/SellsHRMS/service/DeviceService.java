package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.device.DeviceDTO;
import com.sellspark.SellsHRMS.entity.Device;
import java.util.List;

public interface DeviceService {
    Device createDevice(Long organisationId, String name, String deviceCode);

    List<DeviceDTO> getDevicesByOrganisation(Long organisationId);

    Device getDeviceByApiKey(String apiKey);

    DeviceDTO updateDeviceStatus(Long deviceId, Device.Status status);
}
