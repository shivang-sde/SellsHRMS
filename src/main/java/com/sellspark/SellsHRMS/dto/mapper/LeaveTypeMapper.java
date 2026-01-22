package com.sellspark.SellsHRMS.dto.mapper;


import com.sellspark.SellsHRMS.dto.leave.*;
import com.sellspark.SellsHRMS.entity.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {

    public LeaveType toEntity(LeaveTypeRequestDTO dto) {
        return LeaveType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .annualLimit(dto.getAnnualLimit())
                .isPaid(dto.getIsPaid())
                .carryForwardAllowed(dto.getCarryForwardAllowed())
                .carryForwardLimit(dto.getCarryForwardLimit())
                .encashable(dto.getEncashable())
                .requiresApproval(dto.getRequiresApproval())
                .availableDuringProbation(dto.getAvailableDuringProbation())
                .allowHalfDay(dto.getAllowHalfDay())
                .includeHolidaysInLeave(dto.getIncludeHolidaysInLeave())
                .visibleToEmployees(dto.getVisibleToEmployees())
                .maxConsecutiveDays(dto.getMaxConsecutiveDays())
                .build();
    }

    public LeaveTypeResponseDTO toResponseDTO(LeaveType entity) {
        return LeaveTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .annualLimit(entity.getAnnualLimit())
                .isPaid(entity.getIsPaid())
                .carryForwardAllowed(entity.getCarryForwardAllowed())
                .encashable(entity.getEncashable())
                .allowHalfDay(entity.getAllowHalfDay())
                .visibleToEmployees(entity.getVisibleToEmployees())
                .requiresApproval(entity.getRequiresApproval())
                .maxConsecutiveDays(entity.getMaxConsecutiveDays())
                .build();
    }
}
