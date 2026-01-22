package com.sellspark.SellsHRMS.controller.api;

import com.sellspark.SellsHRMS.dto.employee.ShiftDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.Shift;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftRestController {

    private final ShiftService shiftService;
    private final OrganisationRepository organisationRepository;

    @PostMapping
    public Shift create(@RequestBody ShiftDTO dto) {
        Organisation org = organisationRepository.findById(dto.getOrgId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        Shift shift = new Shift();
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());
        shift.setBreakMinutes(dto.getBreakMinutes());
        shift.setIsNightShift(dto.getIsNightShift());
        shift.setOrganisation(org);

        return shiftService.createShift(shift);
    }

    @GetMapping("/org/{orgId}")
    public List<Shift> getAll(@PathVariable Long orgId) {
        return shiftService.getAllShiftsByOrgId(orgId);
    }

    @GetMapping("/{id}")
    public Shift getOne(@PathVariable Long id) {
        return shiftService.getShiftById(id);
    }

    @PatchMapping("/{id}")
    public Shift patch(@PathVariable Long id, @RequestBody ShiftDTO dto) {
        return shiftService.patchUpdateShift(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shiftService.deleteShift(id);
    }
}