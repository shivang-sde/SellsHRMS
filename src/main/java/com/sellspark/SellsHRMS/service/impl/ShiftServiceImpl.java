package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.employee.ShiftDTO;
import com.sellspark.SellsHRMS.entity.Shift;
import com.sellspark.SellsHRMS.repository.ShiftRepository;
import com.sellspark.SellsHRMS.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    @Override
    public Shift createShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    @Override
    public List<Shift> getAllShiftsByOrgId(Long orgId) {
        return shiftRepository.findByOrganisationId(orgId);
    }

    @Override
    public Shift getShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));
    }

    @Override
    public Shift patchUpdateShift(Long id, ShiftDTO dto) {
        Shift shift = getShiftById(id);
        
        if (dto.getName() != null) {
            shift.setName(dto.getName());
        }
        if (dto.getStartTime() != null) {
            shift.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            shift.setEndTime(dto.getEndTime());
        }
        if (dto.getBreakMinutes() != null) {
            shift.setBreakMinutes(dto.getBreakMinutes());
        }
        if (dto.getIsNightShift() != null) {
            shift.setIsNightShift(dto.getIsNightShift());
        }
        
        return shiftRepository.save(shift);
    }

    @Override
    public void deleteShift(Long id) {
        Shift shift = getShiftById(id);
        shiftRepository.delete(shift);
    }
}