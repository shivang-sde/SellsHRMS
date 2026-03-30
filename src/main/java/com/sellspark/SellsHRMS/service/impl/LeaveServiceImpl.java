package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.utils.EmployeeHierarchyUtil;
import com.sellspark.SellsHRMS.utils.LeaveBalanceCalculator;
import com.sellspark.SellsHRMS.dto.leave.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.exception.InvalidDateRangeException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.exception.employee.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.leave.LeaveTypeNotFoundException;
import com.sellspark.SellsHRMS.exception.leave.OverlappingLeaveException;
import com.sellspark.SellsHRMS.exception.organisation.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.LeaveService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final HolidayRepository holidayRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganisationRepository organisationRepository;
    private final OrganisationPolicyRepository organisationPolicyRepository;
    private final EmployeeLeaveBalanceRepository balanceRepository;
    private final LeaveBalanceCalculator leaveBalanceCalculator;
    private final EmployeeHierarchyUtil employeeHierarchyUtil;

    @Override
    public LeaveResponseDTO applyLeave(Long orgId, Long employeeId, LeaveRequestDTO request) {
        log.info("Employee {} applying leave for org {} with request: {}", employeeId, orgId, request);

        Employee emp = employeeRepository.findByIdAndDeletedFalse(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));

        LeaveType leaveType = leaveTypeRepository.findByIdAndOrganisation(request.getLeaveTypeId(), org)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", "id", request.getLeaveTypeId()));

        validateLeaveDates(request.getStartDate(), request.getEndDate());

        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisation(org)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationPolicy", "organisationId", orgId));

        double days = calculateLeaveDays(request, orgId, leaveType);

        validateLeaveAgainstTypePolicy(emp, leaveType, request, days, policy);

        String leaveYear = getCurrentLeaveYear(orgId);
        LocalDate leaveYearStart = getLeaveYearStartDate(orgId);

        log.info("Validating leave balance for emp={}, type={}, leaveYear={}", emp.getId(), leaveType.getId(),
                leaveYear);

        // Use dynamic calculator
        leaveBalanceCalculator.validateLeaveRequest(emp, leaveType, days, leaveYear, leaveYearStart);

        boolean overlapExists = leaveRepository.existsOverlappingLeave(
                employeeId,
                request.getStartDate(),
                request.getEndDate(),
                List.of(Leave.LeaveStatus.PENDING, Leave.LeaveStatus.APPROVE));

        if (overlapExists) {
            // Load overlapping leaves only if broad overlap detected
            List<Leave> overlaps = leaveRepository.findOverlappingLeaves(
                    employeeId,
                    request.getStartDate(),
                    request.getEndDate());

            boolean conflict = overlaps.stream().anyMatch(existing -> {
                // Case 1: exact same single-day range
                if (existing.getStartDate().equals(request.getStartDate()) &&
                        existing.getEndDate().equals(request.getEndDate())) {

                    // Both are half-day → check halves
                    if (request.getStartDayBreakdown() != null && existing.getStartDayBreakdown() != null) {
                        // Allow only opposite halves on the same day
                        Leave.DayBreakdown requestBreakdown = Leave.DayBreakdown
                                .valueOf(request.getStartDayBreakdown());
                        return !(!requestBreakdown.equals(existing.getStartDayBreakdown())
                                && requestBreakdown != Leave.DayBreakdown.FULL_DAY
                                && existing.getStartDayBreakdown() != Leave.DayBreakdown.FULL_DAY);
                    }
                    // If either is full day, it’s an overlap
                    return true;
                }

                // Case 2: multi-day overlap (any range intersection)
                return !(request.getEndDate().isBefore(existing.getStartDate()) ||
                        request.getStartDate().isAfter(existing.getEndDate()));
            });

            if (conflict) {
                // throw new RuntimeException(
                // "You already have a leave applied during this period (including half-day)."
                // );
                throw new OverlappingLeaveException();
            }
        }

        Leave leave = Leave.builder()
                .organisation(org)
                .employee(emp)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .startDayBreakdown(Leave.DayBreakdown.valueOf(request.getStartDayBreakdown()))
                .endDate(request.getEndDate())
                .endDayBreakdown(Leave.DayBreakdown.valueOf(request.getEndDayBreakdown()))
                .leaveDays(days)
                .reason(request.getReason())
                .leaveStatus(Leave.LeaveStatus.PENDING)
                .appliedOn(LocalDate.now())
                .source(Leave.LeaveSource.EMPLOYEE_APPLY)
                .build();

        leaveRepository.save(leave);
        return toResponseDTO(leave);
    }

    @Override
    public LeaveResponseDTO updateLeave(Long leaveId, LeaveRequestDTO leave, Long employeeId, Long orgId) {
        Leave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));

        boolean overlapExists = leaveRepository.existsOverlappingLeave(
                employeeId,
                leave.getStartDate(),
                leave.getEndDate(),
                List.of(Leave.LeaveStatus.PENDING, Leave.LeaveStatus.APPROVE));

        if (overlapExists) {
            List<Leave> overlaps = leaveRepository.findOverlappingLeaves(
                    employeeId,
                    leave.getStartDate(),
                    leave.getEndDate());

            boolean conflict = overlaps.stream()
                    .filter(l -> !l.getId().equals(leaveId)) // exclude same leave
                    .anyMatch(existing -> {
                        // Case 1: exact same single-day range
                        if (existing.getStartDate().equals(leave.getStartDate()) &&
                                existing.getEndDate().equals(leave.getEndDate())) {

                            if (leave.getStartDayBreakdown() != null && existing.getStartDayBreakdown() != null) {
                                Leave.DayBreakdown requestBreakdown = Leave.DayBreakdown
                                        .valueOf(leave.getStartDayBreakdown());
                                return !(!requestBreakdown.equals(existing.getStartDayBreakdown())
                                        && requestBreakdown != Leave.DayBreakdown.FULL_DAY
                                        && existing.getStartDayBreakdown() != Leave.DayBreakdown.FULL_DAY);
                            }
                            return true;
                        }

                        // Case 2: multi-day overlap
                        return !(leave.getEndDate().isBefore(existing.getStartDate()) ||
                                leave.getStartDate().isAfter(existing.getEndDate()));
                    });

            if (conflict) {
                throw new OverlappingLeaveException();
            }
        }

        double leaveDays = calculateLeaveDays(leave, orgId, existingLeave.getLeaveType());
        validateLeaveAgainstTypePolicy(existingLeave.getEmployee(), existingLeave.getLeaveType(), leave, leaveDays,
                organisationPolicyRepository.findByOrganisationId(orgId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("OrganisationPolicy", "organisationId", orgId)));

        String leaveYear = getCurrentLeaveYear(orgId);
        LocalDate leaveYearStart = getLeaveYearStartDate(orgId);
        leaveBalanceCalculator.validateLeaveRequest(
                existingLeave.getEmployee(),
                existingLeave.getLeaveType(),
                leaveDays,
                leaveYear,
                leaveYearStart);

        // Proceed with updating fields
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setStartDayBreakdown(Leave.DayBreakdown.valueOf(leave.getStartDayBreakdown()));
        existingLeave.setEndDate(leave.getEndDate());
        existingLeave.setEndDayBreakdown(Leave.DayBreakdown.valueOf(leave.getEndDayBreakdown()));
        existingLeave.setReason(leave.getReason());
        existingLeave.setLeaveDays(leaveDays);
        existingLeave.setUpdatedAt(LocalDateTime.now());

        leaveRepository.save(existingLeave);
        return toResponseDTO(existingLeave);
    }

    @Override
    public void cancelLeave(Long leaveId, Long employeeId, Long orgId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));
        if (leave.getLeaveStatus() != Leave.LeaveStatus.PENDING)
            throw new InvalidOperationException("Only pending leaves can be canceled.");

        leave.setLeaveStatus(Leave.LeaveStatus.CANCELED);
        leaveRepository.save(leave);
    }

    @Override
    public LeaveResponseDTO approveLeave(Long leaveId, Long approverId, String remarks, Long orgId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            com.sellspark.SellsHRMS.config.UserPrincipal principal = (com.sellspark.SellsHRMS.config.UserPrincipal) auth
                    .getPrincipal();
            if ("ORG_ADMIN".equals(principal.getSystemRole()) || "SUPER_ADMIN".equals(principal.getSystemRole())) {
                throw new InvalidOperationException(
                        "ORG_ADMIN cannot approve or reject leaves. It requires an employee account.");
            }
        }
        log.info("Approving leave for leaveId: {}, approverId: {}, remarks: {}, orgId: {}", leaveId, approverId,
                remarks, orgId);
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));
        if (leave.getLeaveStatus() != Leave.LeaveStatus.PENDING)
            throw new InvalidOperationException("Only pending leaves can be approved.");

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));

        leave.setLeaveStatus(Leave.LeaveStatus.APPROVE);
        leave.setApprovedBy(approver);
        leave.setApprovedOn(LocalDate.now());
        leave.setApproverRemarks(remarks);

        leaveRepository.save(leave);
        String leaveYear = getCurrentLeaveYear(orgId);
        LocalDate leaveYearStart = getLeaveYearStartDate(orgId);
        leaveBalanceCalculator.validateLeaveRequest(
                leave.getEmployee(),
                leave.getLeaveType(),
                leave.getLeaveDays(),
                leaveYear,
                leaveYearStart);

        updateBalanceOnApproval(leave);
        return toResponseDTO(leave);
    }

    @Override
    public LeaveRequestDTO rejectLeave(Long leaveId, Long approverId, String remarks, Long orgId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            com.sellspark.SellsHRMS.config.UserPrincipal principal = (com.sellspark.SellsHRMS.config.UserPrincipal) auth
                    .getPrincipal();
            if ("ORG_ADMIN".equals(principal.getSystemRole()) || "SUPER_ADMIN".equals(principal.getSystemRole())) {
                throw new InvalidOperationException(
                        "ORG_ADMIN cannot approve or reject leaves. It requires an employee account.");
            }
        }
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", leaveId));
        if (leave.getLeaveStatus() != Leave.LeaveStatus.PENDING)
            throw new InvalidOperationException("Only pending leaves can be rejected.");

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new EmployeeNotFoundException(approverId));

        leave.setLeaveStatus(Leave.LeaveStatus.REJECTED);
        leave.setApprovedBy(approver);
        leave.setApprovedOn(LocalDate.now());
        leave.setApproverRemarks(remarks);

        leaveRepository.save(leave);
        return toRequestDTO(leave);
    }

    @Override
    public boolean checkLeave(Long employeeId, LocalDate date) {
        return leaveRepository.existsByEmployeeAndDateAndApproved(employeeId, date);
    }

    @Override
    public LeaveResponseDTO getLeaveById(Long id, Long orgId) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave", "id", id));
        return toResponseDTO(leave);
    }

    public List<LeaveResponseDTO> findVisibleLeaves(Long orgId, Long empId) {
        // Get subordinates recursively
        Set<Long> subordinateIds = employeeHierarchyUtil.getAllSubordinateIds(empId);

        List<Leave> leaves = leaveRepository.findByOrganisationIdAndEmployeeIdIn(orgId, subordinateIds);

        return leaves.stream().map(leave -> {
            LeaveResponseDTO dto = toResponseDTO(leave);
            dto.setCanApprove(true); // always true since they’re subordinates
            return dto;
        }).toList();
    }

    @Override
    public List<LeaveResponseDTO> getAllLeaves(Long orgId, Long empId) {
        List<Leave> allLeaves = leaveRepository.findByOrganisationId(orgId);
        return resolveFilteredLeaves(allLeaves, empId);
    }

    @Override
    public List<LeaveResponseDTO> getEmployeeLeaves(Long employeeId, String leaveYear) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        LocalDate start = getLeaveYearStartDate(emp.getOrganisation().getId());
        LocalDate end = getLeaveYearEndDate(emp.getOrganisation().getId());

        List<Leave> leaves = leaveRepository.findByEmployeeAndDateRange(emp.getId(), start, end);
        return leaves.stream().map(this::toResponseDTO).toList();
    }

    @Override
    public List<LeaveResponseDTO> getPendingLeaves(Long orgId, Long empId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<Leave> pending = leaveRepository.findByOrganisationAndLeaveStatus(org, Leave.LeaveStatus.PENDING);
        return resolveFilteredLeaves(pending, empId);
    }

    @Override
    public List<LeaveResponseDTO> getLeavesByStatus(Long orgId, Long empId, String status) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<Leave> leaves = leaveRepository.findByOrganisationAndLeaveStatus(org, Leave.LeaveStatus.valueOf(status));
        return resolveFilteredLeaves(leaves, empId);
    }

    @Override
    public List<LeaveResponseDTO> getLeavesBetweenDates(Long orgId, Long empId, LocalDate from, LocalDate to) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<Leave> leaves = leaveRepository.findLeavesBetweenDates(org, from, to);
        return resolveFilteredLeaves(leaves, empId);
    }

    /**
     * Resolves which leaves to return based on the current principal's authority:
     * - ORG_ADMIN / SUPER_ADMIN / EMPLOYEE_VIEW_ALL → full list
     * - EMPLOYEE_VIEW_TEAM → only subordinates of the given empId
     * - otherwise → empty list
     */
    private List<LeaveResponseDTO> resolveFilteredLeaves(List<Leave> leaves, Long empId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return java.util.Collections.emptyList();
        }
        com.sellspark.SellsHRMS.config.UserPrincipal principal = (com.sellspark.SellsHRMS.config.UserPrincipal) auth
                .getPrincipal();

        boolean isAdmin = "ORG_ADMIN".equals(principal.getSystemRole())
                || "SUPER_ADMIN".equals(principal.getSystemRole());

        if (isAdmin || principal.hasAnyPermission("EMPLOYEE_VIEW_ALL")) {
            return leaves.stream().map(this::toResponseDTO).toList();
        } else if (principal.hasAnyPermission("EMPLOYEE_VIEW_TEAM")) {
            // Determine effective empId: prefer session-passed empId, fallback to principal
            Long resolvedEmpId = (empId != null) ? empId : principal.getEmployeeId();
            if (resolvedEmpId == null) {
                return java.util.Collections.emptyList();
            }
            Set<Long> subordinateIds = employeeHierarchyUtil.getAllSubordinateIds(resolvedEmpId);
            return leaves.stream()
                    .filter(l -> l.getEmployee() != null && subordinateIds.contains(l.getEmployee().getId()))
                    .map(this::toResponseDTO)
                    .toList();
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public EmployeeLeaveBalance getEmployeeLeaveBalance(Long employeeId, Long leaveTypeId, String ly) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        LeaveType type = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new LeaveTypeNotFoundException(leaveTypeId));
        return balanceRepository.findByEmployeeAndLeaveTypeAndLeaveYear(emp, type, ly)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeLeaveBalance", "employeeId", employeeId));
    }

    @Override
    public List<EmployeeLeaveBalanceDTO> getEmployeeAllBalances(Long employeeId, String leaveYear) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return balanceRepository.findByEmployeeAndLeaveYear(emp, leaveYear)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<EmployeeLeaveBalanceDTO> getOrgEmployeeLeaveBalances(Long orgId) {
        return balanceRepository.findAll().stream()
                .filter(b -> b.getOrganisation().getId().equals(orgId))
                .map(b -> EmployeeLeaveBalanceDTO.builder()
                        .id(b.getId())
                        .employeeId(b.getEmployee().getId())
                        .employeeName(b.getEmployee().getFirstName() + " " + b.getEmployee().getLastName())
                        .employeeCode(b.getEmployee().getEmployeeCode())
                        .departmentName(b.getEmployee().getDepartment() != null
                                ? b.getEmployee().getDepartment().getName()
                                : "-")
                        .leaveTypeId(b.getLeaveType().getId())
                        .leaveTypeName(b.getLeaveType().getName())
                        .isPaid(b.getLeaveType().getIsPaid())
                        .leaveYear(b.getLeaveYear())
                        .openingBalance(b.getOpeningBalance())
                        .accrued(b.getAccrued())
                        .availed(b.getAvailed())
                        .carriedForward(b.getCarriedForward())
                        .encashed(b.getEncashed())
                        .closingBalance(b.getClosingBalance())
                        .build())
                .toList();
    }

    @Override
    public void initializeLeaveBalancesForEmployee(Long employeeId, Long orgId, String leaveYear) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));

        List<LeaveType> leaveTypes = leaveTypeRepository.findByOrganisationAndIsActiveTrue(org);
        if (leaveTypes == null)
            return;

        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisation(org)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));

        for (LeaveType type : leaveTypes) {
            if (emp.isOnProbation() && !type.getAvailableDuringProbation()) {
                continue;
            }

            if (type.getApplicableGender() != LeaveType.ApplicableGender.ALL
                    && !type.getApplicableGender().name().equalsIgnoreCase(emp.getGender().name())) {
                continue;
            }

            balanceRepository.findByEmployeeAndLeaveTypeAndLeaveYear(emp, type, leaveYear)
                    .orElseGet(() -> {
                        double opening = calculateProratedBalance(emp.getDateOfJoining(), type, policy);
                        EmployeeLeaveBalance bal = EmployeeLeaveBalance.builder()
                                .employee(emp)
                                .organisation(org)
                                .leaveType(type)
                                .leaveYear(leaveYear)
                                .openingBalance(opening)
                                .closingBalance(opening)
                                .lastUpdatedOn(LocalDateTime.now())
                                .build();
                        return balanceRepository.save(bal);
                    });
        }
    }

    @Override
    public double calculateProratedBalance(LocalDate joiningDate, LeaveType type, OrganisationPolicy policy) {
        if (type.getAnnualLimit() == null)
            return 0.0;

        LocalDate lyStart = getLeaveYearStartDate(policy.getOrganisation().getId());
        if (joiningDate.isBefore(lyStart))
            return type.getAnnualLimit();

        long monthsRemaining = ChronoUnit.MONTHS.between(
                YearMonth.from(joiningDate),
                YearMonth.from(lyStart.plusYears(1)));
        monthsRemaining = Math.max(0, Math.min(12, monthsRemaining));

        return (type.getAnnualLimit() / 12.0) * monthsRemaining;
    }

    @Override
    public LocalDate getFYStartDate(Long orgId) {
        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisationId(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationPolicy", "organisationId", orgId));

        // Default FY start month = April (4)
        int startMonth = Optional.ofNullable(policy.getFinancialYearStartMonth()).orElse(4);

        LocalDate today = LocalDate.now();
        int startYear = (today.getMonthValue() >= startMonth) ? today.getYear() : today.getYear() - 1;

        return LocalDate.of(startYear, startMonth, 1);
    }

    @Override
    public LocalDate getFYEndDate(Long orgId) {
        return getFYStartDate(orgId).plusYears(1).minusDays(1);
    }

    @Override
    public LocalDate getLeaveYearStartDate(Long orgId) {
        OrganisationPolicy policy = organisationPolicyRepository.findByOrganisationId(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("OrganisationPolicy", "organisationId", orgId));

        log.info("Organisation Policy retrieved: {}", policy);

        // Prefer leave-year start; fallback to FY start if null; default = April (4)
        int startMonth = Optional.ofNullable(policy.getLeaveYearStartMonth())
                .orElse(Optional.ofNullable(policy.getFinancialYearStartMonth()).orElse(4));

        LocalDate today = LocalDate.now();
        int startYear = (today.getMonthValue() >= startMonth) ? today.getYear() : today.getYear() - 1;

        return LocalDate.of(startYear, startMonth, 1);
    }

    @Override
    public LocalDate getLeaveYearEndDate(Long orgId) {
        return getLeaveYearStartDate(orgId).plusYears(1).minusDays(1);
    }

    @Override
    public void validateLeaveAgainstTypePolicy(Employee emp, LeaveType type, LeaveRequestDTO req, double days,
            OrganisationPolicy policy) {

        // 1️⃣ Gender applicability
        if (type.getApplicableGender() != LeaveType.ApplicableGender.ALL &&
                !emp.getGender().toString().equalsIgnoreCase(type.getApplicableGender().name())) {
            throw new InvalidOperationException("This leave type is not applicable to your gender.");
        }

        if (Boolean.TRUE.equals(type.getAvailableDuringProbation())) {
            // Disallow leave types like maternity/paternity if they exist
            String lowerName = type.getName().toLowerCase();
            if (lowerName.contains("maternity") || lowerName.contains("paternity")) {
                throw new InvalidOperationException("Maternity/Paternity leaves cannot be available during probation.");
            }
        }

        // 2️⃣ Probation restriction
        if (emp.isOnProbation() && !Boolean.TRUE.equals(type.getAvailableDuringProbation())) {
            throw new InvalidOperationException("This leave type is not available during probation.");
        }

        // 3️⃣ Notice period restriction
        if (emp.getServiceStage() == Employee.ServiceStage.NOTICE
                && !Boolean.TRUE.equals(type.getAllowDuringNoticePeriod())) {
            throw new InvalidOperationException("This leave type cannot be availed during notice period.");
        }

        // 4️⃣ Half-day applicability
        if (Boolean.TRUE.equals(req.getIsHalfDay()) && !Boolean.TRUE.equals(type.getAllowHalfDay())) {
            throw new InvalidOperationException("Half-day leave not allowed for this type.");
        }

        // 5️⃣ Consecutive day limit
        if (type.getMaxConsecutiveDays() != null && days > type.getMaxConsecutiveDays()) {
            throw new InvalidOperationException(
                    "You cannot apply more than " + type.getMaxConsecutiveDays() + " consecutive days.");
        }

        // 6️⃣ Requires approval or not
        if (!Boolean.TRUE.equals(type.getRequiresApproval())) {
            log.info("Auto-approval: Leave '{}' does not require manual approval", type.getName());
        }

        // 7️⃣ Include / exclude holidays (handled during day calculation)
        if (!Boolean.TRUE.equals(type.getIncludeHolidaysInLeave())) {
            log.debug("Holidays excluded from leave duration for type: {}", type.getName());
        }

        // 8️⃣ Validity days (for comp-off, special leaves)
        if (type.getValidityDays() != null) {
            LocalDate expiry = req.getStartDate().plusDays(type.getValidityDays());
            if (LocalDate.now().isAfter(expiry)) {
                throw new InvalidOperationException(
                        "This leave type expired after " + type.getValidityDays() + " days of accrual.");
            }
        }
    }

    // ========================
    // 2. LEAVE BALANCE MANAGEMENT
    // ========================

    @Override
    public void initializeNewLeaveTypeBalances(LeaveType leaveType, String leaveYear, Long orgId) {
        List<Employee> employees = employeeRepository.findByOrganisationIdAndDeletedFalse(orgId);

        for (Employee emp : employees) {
            balanceRepository.findByEmployeeAndLeaveTypeAndLeaveYear(emp, leaveType, leaveYear)
                    .orElseGet(() -> {
                        EmployeeLeaveBalance bal = EmployeeLeaveBalance.builder()
                                .employee(emp)
                                .organisation(leaveType.getOrganisation())
                                .leaveType(leaveType)
                                .leaveYear(leaveYear)
                                .openingBalance(
                                        leaveType.getAnnualLimit() != null ? leaveType.getAnnualLimit().doubleValue()
                                                : 0.0)
                                .closingBalance(
                                        leaveType.getAnnualLimit() != null ? leaveType.getAnnualLimit().doubleValue()
                                                : 0.0)
                                .lastUpdatedOn(LocalDateTime.now())
                                .build();
                        return balanceRepository.save(bal);
                    });
        }
        ;
    }

    @Override
    public void accrueMonthlyLeaves(Long orgId, String leaveYear) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<EmployeeLeaveBalance> balances = balanceRepository.findAll();
        for (EmployeeLeaveBalance bal : balances) {
            if (bal.getOrganisation().equals(org)) {
                double rate = bal.getLeaveType().getAccrualRate() != null ? bal.getLeaveType().getAccrualRate() : 0;
                bal.setAccrued(bal.getAccrued() + rate);
                bal.setClosingBalance(bal.getClosingBalance() + rate);
                bal.setLastUpdatedOn(LocalDateTime.now());
                balanceRepository.save(bal);
            }
        }
    }

    // ========================
    // 3. CALCULATE LEAVE DAYS (Holidays, Half-Days)
    // ========================

    // @Override
    // public Double calculateLeaveDays(LocalDate startDate, LocalDate endDate, Long
    // orgId) {
    // long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    // return (double) totalDays;
    // }

    private boolean isHalf(String s) {
        return s != null && (s.equalsIgnoreCase("FIRST_HALF") || s.equalsIgnoreCase("SECOND_HALF"));
    }

    @Override
    public double calculateLeaveDays(LeaveRequestDTO req, Long orgId, LeaveType leaveType) {
        long totalDays = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<Holiday> holidays = holidayRepository.findByOrganisationAndHolidayDateBetween(org, req.getStartDate(),
                req.getEndDate());
        if (!Boolean.TRUE.equals(leaveType.getIncludeHolidaysInLeave())) {
            totalDays -= holidays.size();
        }

        // half-day handling:
        if (req.getStartDate().equals(req.getEndDate())) {
            // single day
            if (isHalf(req.getStartDayBreakdown()) || isHalf(req.getEndDayBreakdown())) {
                if (isHalf(req.getStartDayBreakdown()) && isHalf(req.getEndDayBreakdown())
                        && !req.getStartDayBreakdown().equalsIgnoreCase(req.getEndDayBreakdown())) {
                    return 1.0; // opposite halves = full day
                }
                return 0.5;
            }
            return Math.max(totalDays, 1.0);
        } else {
            double adjustment = 0.0;
            if (isHalf(req.getStartDayBreakdown()))
                adjustment -= 0.5;
            if (isHalf(req.getEndDayBreakdown()))
                adjustment -= 0.5;
            return Math.max(totalDays + adjustment, 0.5);
        }
    }

    @Override
    public String getCurrentFinancialYear(Long orgId) {
        LocalDate start = getFYStartDate(orgId);
        LocalDate end = getFYEndDate(orgId);
        return String.format("FY%d-%02d", start.getYear(), end.getYear() % 100);
    }

    @Override
    public String getCurrentLeaveYear(Long orgId) {
        LocalDate start = getLeaveYearStartDate(orgId);
        LocalDate end = getLeaveYearEndDate(orgId);
        return String.format("LY%d-%02d", start.getYear(), end.getYear() % 100);
    }

    @Override
    public Map<String, Object> getLeaveStatistics(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new OrganisationNotFoundException(orgId));
        List<Leave> leaves = leaveRepository.findByOrganisationId(orgId);
        Map<String, Long> stats = new HashMap<>();
        for (Leave.LeaveStatus status : Leave.LeaveStatus.values()) {
            stats.put(status.name(), leaves.stream().filter(l -> l.getLeaveStatus() == status).count());
        }
        return Map.of("totalLeaves", leaves.size(), "statusStats", stats);
    }

    @Override
    public Map<String, Object> getEmployeeLeaveStats(Long employeeId, String leaveYear) {
        List<LeaveResponseDTO> leaves = getEmployeeLeaves(employeeId, leaveYear);
        Map<String, Long> stats = new HashMap<>();
        for (Leave.LeaveStatus status : Leave.LeaveStatus.values()) {
            stats.put(status.name(), leaves.stream().filter(l -> l.getStatus().equals(status.name())).count());
        }
        return Map.of("totalLeaves", leaves.size(), "statusStats", stats);
    }

    // ====== Helper Methods ======
    @Override
    public void validateLeaveDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end))
            throw new InvalidDateRangeException("Start date cannot be after end date.");
        if (start.isBefore(LocalDate.now()))
            throw new InvalidDateRangeException("Cannot apply leave for past dates.");
    }

    // private void checkLeaveBalance(Employee emp, LeaveType type, double
    // requested, String ly) {
    // EmployeeLeaveBalance bal = balanceRepository
    // .findByEmployeeIdAndLeaveTypeIdAndLeaveYear(emp.getId(), type.getId(), ly)
    // .or(() ->
    // balanceRepository.findTopByEmployeeIdAndLeaveTypeIdOrderByIdDesc(emp.getId(),
    // type.getId()))
    // .orElseThrow(() -> new ResourceNotFoundException(
    // "Leave balance not found for emp=" + emp.getId() + ", type=" + type.getId() +
    // ", year=" + ly));
    // double remaining = bal.getClosingBalance() - bal.getAvailed();
    // if (requested > remaining)
    // throw new InsufficientLeaveBalanceException(requested,
    // bal.getClosingBalance());
    // }

    private void updateBalanceOnApproval(Leave leave) {
        String ly = getCurrentLeaveYear(leave.getEmployee().getOrganisation().getId());
        EmployeeLeaveBalance bal = balanceRepository
                .findByEmployeeAndLeaveTypeAndLeaveYear(leave.getEmployee(), leave.getLeaveType(), ly)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeLeaveBalance", "employeeId",
                        leave.getEmployee().getId()));
        bal.setAvailed(bal.getAvailed() + leave.getLeaveDays());
        bal.setClosingBalance(bal.getClosingBalance() - leave.getLeaveDays());
        bal.setLastUpdatedOn(LocalDateTime.now());
        balanceRepository.save(bal);
    }

    private LeaveResponseDTO toResponseDTO(Leave leave) {

        return LeaveResponseDTO.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName())
                .leaveTypeId(leave.getLeaveType().getId())
                .leaveTypeName(leave.getLeaveType().getName())
                .startDate(leave.getStartDate())
                .startDayBreakdown(
                        leave.getStartDayBreakdown() != null ? leave.getStartDayBreakdown().toString() : null)
                .endDate(leave.getEndDate())
                .endDayBreakdown(leave.getEndDayBreakdown() != null ? leave.getEndDayBreakdown().toString() : null)
                .leaveDays(leave.getLeaveDays())
                .reason(leave.getReason())
                .status(leave.getLeaveStatus().toString())
                .appliedOn(leave.getAppliedOn())
                .approvedOn(leave.getApprovedOn() != null ? leave.getApprovedOn() : null)
                .approverById(leave.getApprovedBy() != null ? leave.getApprovedBy().getId() : null)
                .approverName(leave.getApprovedBy() != null
                        ? leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName()
                        : null)
                .approverRemarks(leave.getApproverRemarks())
                .leaveYear(leave.getLeaveYear())
                .build();
    }

    private LeaveRequestDTO toRequestDTO(Leave leave) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setLeaveTypeId(leave.getLeaveType().getId());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setReason(leave.getReason());
        return dto;
    }

    public EmployeeLeaveBalanceDTO toDto(EmployeeLeaveBalance balance) {
        return EmployeeLeaveBalanceDTO.builder()
                .id(balance.getId())
                .employeeId(balance.getEmployee().getId())
                .employeeName(balance.getEmployee().getFirstName())
                .employeeCode(balance.getEmployee().getEmployeeCode()) // if exists
                .departmentName(balance.getEmployee().getDepartment().getName()) // if exists
                .leaveTypeId(balance.getLeaveType().getId())
                .leaveTypeName(balance.getLeaveType().getName())
                .isPaid(balance.getLeaveType().getIsPaid())
                .leaveYear(balance.getLeaveYear())
                .openingBalance(balance.getOpeningBalance())
                .accrued(balance.getAccrued())
                .availed(balance.getAvailed())
                .carriedForward(balance.getCarriedForward())
                .encashed(balance.getEncashed())
                .closingBalance(balance.getClosingBalance())
                .build();
    }

}
