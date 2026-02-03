package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.leave.LeaveTypeRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveTypeResponseDTO;
import com.sellspark.SellsHRMS.entity.EmployeeLeaveBalance;
import com.sellspark.SellsHRMS.entity.Leave;
import com.sellspark.SellsHRMS.entity.LeaveType;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.DuplicateResourceException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.organisation.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeLeaveBalanceRepository;
import com.sellspark.SellsHRMS.repository.LeaveRepository;
import com.sellspark.SellsHRMS.repository.LeaveTypeRepository;
import com.sellspark.SellsHRMS.repository.OrganisationPolicyRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.LeaveService;
import com.sellspark.SellsHRMS.service.LeaveTypeService;

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

        if (leaveTypeRepository.existsByOrganisationAndNameIgnoreCase(org, request.getName())) {
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

        validateLeaveTypeConfiguration(leaveType);

        LeaveType saved = leaveTypeRepository.save(leaveType);

        String ly = leaveService.getCurrentLeaveYear(request.getOrgId());
        leaveService.initializeNewLeaveTypeBalances(leaveType, ly, org.getId());
        return toResponseDTO(saved);
    }

    @Override
    public List<LeaveTypeResponseDTO> getAllLeaveTypesByOrg(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));

        return leaveTypeRepository.findByOrganisation(org)
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

        if (request.getName() != null)
            leaveType.setName(request.getName());
        if (request.getDescription() != null)
            leaveType.setDescription(request.getDescription());
        if (request.getAnnualLimit() != null)
            leaveType.setAnnualLimit(request.getAnnualLimit());
        if (request.getIsPaid() != null)
            leaveType.setIsPaid(request.getIsPaid());
        if (request.getCarryForwardAllowed() != null)
            leaveType.setCarryForwardAllowed(request.getCarryForwardAllowed());
        if (request.getCarryForwardLimit() != null)
            leaveType.setCarryForwardLimit(request.getCarryForwardLimit());
        if (request.getEncashable() != null)
            leaveType.setEncashable(request.getEncashable());
        if (request.getRequiresApproval() != null)
            leaveType.setRequiresApproval(request.getRequiresApproval());
        if (request.getAvailableDuringProbation() != null)
            leaveType.setAvailableDuringProbation(request.getAvailableDuringProbation());
        if (request.getAllowHalfDay() != null)
            leaveType.setAllowHalfDay(request.getAllowHalfDay());
        if (request.getIncludeHolidaysInLeave() != null)
            leaveType.setIncludeHolidaysInLeave(request.getIncludeHolidaysInLeave());
        if (request.getVisibleToEmployees() != null)
            leaveType.setVisibleToEmployees(request.getVisibleToEmployees());
        if (request.getMaxConsecutiveDays() != null)
            leaveType.setMaxConsecutiveDays(request.getMaxConsecutiveDays());
        if (request.getAccrualMethod() != null)
            leaveType.setAccrualMethod(LeaveType.AccrualMethod.valueOf(request.getAccrualMethod()));
        if (request.getAccrualRate() != null)
            leaveType.setAccrualRate(request.getAccrualRate());
        if (request.getValidityDays() != null)
            leaveType.setValidityDays(request.getValidityDays());
        if (request.getApplicableGender() != null)
            leaveType.setApplicableGender(LeaveType.ApplicableGender.valueOf(request.getApplicableGender()));

        LeaveType updated = leaveTypeRepository.save(leaveType);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        // Check if any approved or pending leaves exist
        boolean inUse = leaveRepository.existsByOrganisationAndLeaveStatusIn(
                leaveType.getOrganisation(),
                List.of(Leave.LeaveStatus.APPROVE, Leave.LeaveStatus.PENDING));

        if (inUse) {
            // If active leaves exist, don't delete; just deactivate
            leaveType.setIsActive(false);
            leaveTypeRepository.save(leaveType);
            log.warn("LeaveType {} is in use; marked inactive instead of deletion", leaveType.getName());
            return;
        }

        // ✅ Delete employee leave balances before deleting the leave type
        List<EmployeeLeaveBalance> balances = employeeLeaveBalanceRepository.findByLeaveTypeId(leaveType.getId());
        if (!balances.isEmpty()) {
            for (EmployeeLeaveBalance bal : balances) {
                log.info("Deleting leave balance {} for leave type {}", bal.getId(), leaveType.getName());
                employeeLeaveBalanceRepository.delete(bal);
            }
        }

        // ✅ Delete the leave type safely
        leaveTypeRepository.delete(leaveType);
        log.info("LeaveType {} deleted successfully", leaveType.getName());
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
                .isActive(entity.getIsActive())
                .build();
    }

    void deleteAllEmpLeaveBalForLeaveType(LeaveType leaveType) {
        List<EmployeeLeaveBalance> employeeLeaveBalances = employeeLeaveBalanceRepository
                .findByLeaveTypeId(leaveType.getId());

        for (EmployeeLeaveBalance employeeLeaveBalance : employeeLeaveBalances) {
            log.info("deleting leave balance {} for lave type {}", employeeLeaveBalance.getId(),
                    employeeLeaveBalance.getLeaveType().getName());
            employeeLeaveBalanceRepository.delete(employeeLeaveBalance);
        }
    }

    private void validateLeaveTypeConfiguration(LeaveType type) {

        // ---- Accrual / Carry-forward relationships ----
        if (type.getAccrualMethod() == LeaveType.AccrualMethod.MONTHLY) {
            if (Boolean.TRUE.equals(type.getCarryForwardAllowed()) &&
                    type.getCarryForwardLimit() != null &&
                    type.getAccrualRate() != null &&
                    type.getCarryForwardLimit() > type.getAccrualRate()) {
                throw new InvalidOperationException("Carry forward limit cannot exceed monthly accrual rate.");
            }
        } else {
            type.setCarryForwardAllowed(false);
            type.setCarryForwardLimit(0);
        }

        // ---- Field dependencies ----
        if (type.getAccrualMethod() == LeaveType.AccrualMethod.NONE &&
                type.getAccrualRate() != null && type.getAccrualRate() > 0) {
            throw new InvalidOperationException("Accrual rate not applicable for NONE accrual method.");
        }

        if (type.getAccrualMethod() == LeaveType.AccrualMethod.ANNUAL &&
                (type.getAnnualLimit() == null || type.getAnnualLimit() <= 0)) {
            throw new InvalidOperationException("Annual limit required for annual accrual method.");
        }

        if (type.getValidityDays() != null && type.getValidityDays() > 0 &&
                type.getAccrualMethod() != LeaveType.AccrualMethod.NONE) {
            throw new InvalidOperationException("Validity days only apply for special/comp-off leaves.");
        }

        if (type.getMaxConsecutiveDays() != null && type.getAnnualLimit() != null) {
            if (type.getMaxConsecutiveDays() > type.getAnnualLimit()) {
                throw new InvalidOperationException("Max consecutive days cannot exceed annual limit.");
            }
        }
    }

}
