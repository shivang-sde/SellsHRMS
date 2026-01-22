package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.employee.ShiftDTO;
import com.sellspark.SellsHRMS.entity.Shift;
import java.util.List;

public interface ShiftService {
    Shift createShift(Shift shift);
    List<Shift> getAllShiftsByOrgId(Long orgId);
    Shift getShiftById(Long id);
    Shift patchUpdateShift(Long id, ShiftDTO dto);
    void deleteShift(Long id);
}