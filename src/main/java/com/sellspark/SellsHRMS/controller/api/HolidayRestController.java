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

import com.sellspark.SellsHRMS.service.HolidayService; // Import Service

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayRestController {

    private final HolidayRepository holidayRepo;
    private final OrganisationRepository orgRepo;
    private final HolidayService holidayService; // Inject Service

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

    @PostMapping("/org/{orgId}/bulk")
    public ResponseEntity<List<HolidayResponse>> createBulk(
            @PathVariable Long orgId,
            @RequestBody List<HolidayRequest> requests) {
        // Service handles the logic
        // We need to inject HolidayService instead of Repo?
        // Wait, the controller is using Repositories currently?
        // The snippet in prompt shows `HolidayRepository` and `OrganisationRepository`.
        // BUT there is a `HolidayService`. I should refactor to use Service ideally,
        // OR just implement logic here?
        // The prompt says "I want to add feature...".
        // The `HolidayServiceImpl` I just modified IS the service.
        // The controller in prompt `HolidayRestController` uses REPOS directly?
        // Let's check the controller imports/fields in the prompt carefully.
        // Yes, `private final HolidayRepository holidayRepo; private final
        // OrganisationRepository orgRepo;`
        // It does NOT use `HolidayService`.
        // This is inconsistent. I should check if the user WANTS me to refactor or just
        // add features.
        // The user edited `HolidayServiceImpl`, so there IS a service.
        // I should update the Controller to USE the Service for this new feature at
        // least,
        // to avoid duplicating the Upsert logic I just wrote in Service.

        // I need to Inject `HolidayService` into `HolidayRestController`.
        // The current controller uses `@RequiredArgsConstructor`.
        // I will add `private final HolidayService holidayService;` to the fields.

        return ResponseEntity.ok(holidayService.createBulkHolidays(orgId, requests));
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<HolidayResponse>> getAllByOrg(@PathVariable Long orgId) {
        List<Holiday> holidays = holidayRepo.findByOrganisationId(orgId);
        return ResponseEntity.ok(
                holidays.stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()));
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
                        .collect(Collectors.toList()));
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
