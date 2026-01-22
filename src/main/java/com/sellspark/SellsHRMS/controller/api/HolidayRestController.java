 package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.holiday.HolidayRequest;
import com.sellspark.SellsHRMS.dto.holiday.HolidayResponse;
import com.sellspark.SellsHRMS.entity.Holiday;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.HolidayRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayRestController {

    private final HolidayRepository holidayRepo;
    private final OrganisationRepository orgRepo;

    @PostMapping
    public ResponseEntity<HolidayResponse> create(@RequestBody HolidayRequest request) {
        Organisation org = orgRepo.findById(request.getOrgId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        Holiday holiday = Holiday.builder()
                .organisation(org)
                .holidayDate(request.getHolidayDate())
                .holidayName(request.getHolidayName())
                .holidayType(Holiday.HolidayType.valueOf(request.getHolidayType()))
                .isMandatory(request.getIsMandatory())
                .description(request.getDescription())
                .build();

        holiday = holidayRepo.save(holiday);
        return ResponseEntity.ok(mapToResponse(holiday));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<HolidayResponse>> getAllByOrg(@PathVariable Long orgId) {
        List<Holiday> holidays = holidayRepo.findByOrganisationId(orgId);
        return ResponseEntity.ok(
            holidays.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList())
        );
    }

    @GetMapping("/org/{orgId}/range")
    public ResponseEntity<List<HolidayResponse>> getByDateRange(
            @PathVariable Long orgId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        
        List<Holiday> holidays = holidayRepo
                .findByOrganisationIdAndHolidayDateBetween(orgId, startDate, endDate);
        
        return ResponseEntity.ok(
            holidays.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        holidayRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private HolidayResponse mapToResponse(Holiday holiday) {
        HolidayResponse response = new HolidayResponse();
        response.setId(holiday.getId());
        response.setOrgId(holiday.getOrganisation().getId());
        response.setHolidayDate(holiday.getHolidayDate());
        response.setHolidayName(holiday.getHolidayName());
        response.setHolidayType(holiday.getHolidayType().name());
        response.setIsMandatory(holiday.getIsMandatory());
        response.setDescription(holiday.getDescription());
        return response;
    }
}
