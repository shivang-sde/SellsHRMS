package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.leave.LeaveTypeRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveTypeResponseDTO;

import java.util.List;


public interface LeaveTypeService {
    LeaveTypeResponseDTO createLeaveType(LeaveTypeRequestDTO request);
    List<LeaveTypeResponseDTO> getAllLeaveTypesByOrg(Long orgId);
    LeaveTypeResponseDTO getLeaveTypeByIdAndOrg(Long id, Long orgId);
    LeaveTypeResponseDTO updateLeaveType(Long id, LeaveTypeRequestDTO request);
    void deleteLeaveType(Long id);
}
    