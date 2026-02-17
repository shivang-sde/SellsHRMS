package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.attendance.DevicePunchRequest;
import com.sellspark.SellsHRMS.dto.attendance.PunchRecordResponse;
import com.sellspark.SellsHRMS.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceAttendanceRestController {

    private final AttendanceService attendanceService;

    @PostMapping("/punch")
    public ResponseEntity<PunchRecordResponse> punch(@RequestHeader("X-API-KEY") String apiKey,
            @RequestBody DevicePunchRequest request) {
        return ResponseEntity.ok(attendanceService.processDevicePunch(apiKey, request));
    }
}
