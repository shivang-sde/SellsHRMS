package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.leave.LeaveTypeRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveTypeResponseDTO;
import com.sellspark.SellsHRMS.entity.EmployeeLeaveBalance;
import com.sellspark.SellsHRMS.entity.Leave;
import com.sellspark.SellsHRMS.entity.LeaveType;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.DuplicateResourceException;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeLeaveBalanceRepository;
import com.sellspark.SellsHRMS.repository.LeaveRepository;
import com.sellspark.SellsHRMS.repository.LeaveTypeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.LeaveService;
import com.sellspark.SellsHRMS.service.LeaveTypeService;
import com.sellspark.SellsHRMS.service.OrganisationPolicyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final OrganisationRepository organisationRepository;
    private final OrganisationPolicyRepository organisationPolicyRepository;
    private final LeaveRepository leaveRepository;
    private final LeaveService leaveService;
    private final EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;

    @Override
    public LeaveTypeResponseDTO createLeaveType(LeaveTypeRequestDTO request) {
        Organisation org = organisationRepository.findById(request.getOrgId())
                .orElseThrow(() -> new OrganisationNotFoundException(request.getOrgId()));

        if (leaveTypeRepository.existsByOrganisationAndName(org, request.getName())) {
            throw new DuplicateResourceException("Leave Type", "name", request.getName());
        }

        LeaveType leaveType = LeaveType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .annualLimit(request.getAnnualLimit())
                .isPaid(request.getIsPaid())
                .carryForwardAllowed(request.getCarryForwardAllowed())
                .carryForwardLimit(request.getCarryForwardLimit())
                .encashable(request.getEncashable())
                .requiresApproval(request.getRequiresApproval())
                .availableDuringProbation(request.getAvailableDuringProbation())
                .allowHalfDay(request.getAllowHalfDay())
                .includeHolidaysInLeave(request.getIncludeHolidaysInLeave())
                .visibleToEmployees(request.getVisibleToEmployees())
                .maxConsecutiveDays(request.getMaxConsecutiveDays())
                .accrualMethod(LeaveType.AccrualMethod.valueOf(request.getAccrualMethod()))
                .applicableGender(LeaveType.ApplicableGender.valueOf(request.getApplicableGender()))
                .accrualRate(request.getAccrualRate())
                .validityDays(request.getValidityDays())
                .organisation(org)
                .isActive(true)
                .build();

        LeaveType saved = leaveTypeRepository.save(leaveType);

       String ly = leaveService.getCurrentLeaveYear(request.getOrgId());
       leaveService.initializeNewLeaveTypeBalances(leaveType, ly, org.getId());
        return toResponseDTO(saved);
    }

    @Override
    public List<LeaveTypeResponseDTO> getAllLeaveTypesByOrg(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));

        return leaveTypeRepository.findByOrganisationAndIsActiveTrue(org)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeResponseDTO getLeaveTypeByIdAndOrg(Long id, Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        LeaveType leaveType = leaveTypeRepository.findByIdAndOrganisation(id, org)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));
        return toResponseDTO(leaveType);
    }

    @Override
    public LeaveTypeResponseDTO updateLeaveType(Long id, LeaveTypeRequestDTO request) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        if (request.getName() != null) leaveType.setName(request.getName());
        if (request.getDescription() != null) leaveType.setDescription(request.getDescription());
        if (request.getAnnualLimit() != null) leaveType.setAnnualLimit(request.getAnnualLimit());
        if (request.getIsPaid() != null) leaveType.setIsPaid(request.getIsPaid());
        if (request.getCarryForwardAllowed() != null) leaveType.setCarryForwardAllowed(request.getCarryForwardAllowed());
        if (request.getCarryForwardLimit() != null) leaveType.setCarryForwardLimit(request.getCarryForwardLimit());
        if (request.getEncashable() != null) leaveType.setEncashable(request.getEncashable());
        if (request.getRequiresApproval() != null) leaveType.setRequiresApproval(request.getRequiresApproval());
        if (request.getAvailableDuringProbation() != null) leaveType.setAvailableDuringProbation(request.getAvailableDuringProbation());
        if (request.getAllowHalfDay() != null) leaveType.setAllowHalfDay(request.getAllowHalfDay());
        if (request.getIncludeHolidaysInLeave() != null) leaveType.setIncludeHolidaysInLeave(request.getIncludeHolidaysInLeave());
        if (request.getVisibleToEmployees() != null) leaveType.setVisibleToEmployees(request.getVisibleToEmployees());
        if (request.getMaxConsecutiveDays() != null) leaveType.setMaxConsecutiveDays(request.getMaxConsecutiveDays());
        if (request.getAccrualMethod() != null) leaveType.setAccrualMethod(LeaveType.AccrualMethod.valueOf(request.getAccrualMethod()));
        if (request.getAccrualRate() != null) leaveType.setAccrualRate(request.getAccrualRate());
        if (request.getValidityDays() != null) leaveType.setValidityDays(request.getValidityDays());
        if (request.getApplicableGender() != null) leaveType.setApplicableGender(LeaveType.ApplicableGender.valueOf(request.getApplicableGender()));

        LeaveType updated = leaveTypeRepository.save(leaveType);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));
        boolean inUse = leaveRepository.existsByOrganisationAndLeaveStatusIn(
                leaveType.getOrganisation(),
                List.of(Leave.LeaveStatus.APPROVE, Leave.LeaveStatus.PENDING)
        );
        if (inUse) {
            leaveType.setIsActive(false);
            leaveTypeRepository.save(leaveType);
        } else {
            leaveTypeRepository.delete(leaveType);
            
        }
    }

    private LeaveTypeResponseDTO toResponseDTO(LeaveType entity) {
        return LeaveTypeResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .annualLimit(entity.getAnnualLimit())
                .isPaid(entity.getIsPaid())
                .carryForwardAllowed(entity.getCarryForwardAllowed())
                .carryForwardLimit(entity.getCarryForwardLimit())
                .encashable(entity.getEncashable())
                .requiresApproval(entity.getRequiresApproval())
                .availableDuringProbation(entity.getAvailableDuringProbation())
                .allowHalfDay(entity.getAllowHalfDay())
                .includeHolidaysInLeave(entity.getIncludeHolidaysInLeave())
                .visibleToEmployees(entity.getVisibleToEmployees())
                .maxConsecutiveDays(entity.getMaxConsecutiveDays())
                .accrualMethod(entity.getAccrualMethod().toString())
                .applicableGender(entity.getApplicableGender().toString())
                .accrualRate(entity.getAccrualRate())
                .validityDays(entity.getValidityDays())
                .orgId(entity.getOrganisation().getId())
                .build();
    }

    void deleteAllEmpLeaveBalForLeaveType(LeaveType leaveType){
        List<EmployeeLeaveBalance> employeeLeaveBalances = employeeLeaveBalanceRepository.findByLeaveTypeId(leaveType.getId());
        
        for (EmployeeLeaveBalance employeeLeaveBalance : employeeLeaveBalances) {
            log.info("deleting leave balance {} for lave type {}", employeeLeaveBalance.getId(), employeeLeaveBalance.getLeaveType().getName());
            employeeLeaveBalanceRepository.delete(employeeLeaveBalance);
        }
    }
}
