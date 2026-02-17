package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.device.DeviceDTO;
import com.sellspark.SellsHRMS.entity.Device;
import com.sellspark.SellsHRMS.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devices")
@RequiredArgsConstructor
public class DeviceManagementRestController {

    private final DeviceService deviceService;

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(deviceService.getDevicesByOrganisation(orgId));
    }

    @PostMapping("/org/{orgId}")
    public ResponseEntity<Device> createDevice(@PathVariable Long orgId,
            @RequestParam String name,
            @RequestParam String deviceCode) {
        return ResponseEntity.ok(deviceService.createDevice(orgId, name, deviceCode));
    }
}
