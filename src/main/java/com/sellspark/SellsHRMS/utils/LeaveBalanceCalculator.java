package com.sellspark.SellsHRMS.utils;

import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.LeaveType;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.leave.InsufficientLeaveBalanceException;
import com.sellspark.SellsHRMS.repository.EmployeeLeaveBalanceRepository;
import com.sellspark.SellsHRMS.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveBalanceCalculator {

    private final LeaveRepository leaveRepository;
    private final EmployeeLeaveBalanceRepository balanceRepository;

    /**
     * Calculates how many leaves are available to an employee till current date.
     *
     * Logic:
     * - For MONTHLY accrual: (monthsElapsed * accrualRate) + allowed carry-forward
     * - usedSoFar
     * - For ANNUAL accrual: (annualLimit / 12 * monthsElapsed) - usedSoFar
     */
    public double calculateAvailableBalance(Employee emp, LeaveType type, String leaveYear, LocalDate leaveYearStart) {
        double accrualRate = Optional.ofNullable(type.getAccrualRate()).orElse(0.0);
        double carryForwardLimit = Optional.ofNullable(type.getCarryForwardLimit()).map(Integer::doubleValue)
                .orElse(0.0);
        double annualLimit = Optional.ofNullable(type.getAnnualLimit()).orElse(0);

        double usedSoFar = Optional.ofNullable(
                leaveRepository.sumApprovedLeaveDays(emp.getId(), type.getId(), leaveYear)).orElse(0.0);

        double carriedForward = balanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndLeaveYear(emp.getId(), type.getId(), leaveYear)
                .map(b -> Optional.ofNullable(b.getCarriedForward()).orElse(0.0))
                .orElse(0.0);

        // Months since leave year start
        long monthsElapsed = ChronoUnit.MONTHS.between(
                YearMonth.from(leaveYearStart), YearMonth.from(LocalDate.now())) + 1; // include current month

        double totalAccrued = 0.0;
        double allowedCarryForward = 0.0;

        switch (type.getAccrualMethod()) {
            case MONTHLY -> {
                totalAccrued = monthsElapsed * accrualRate;

                if (Boolean.TRUE.equals(type.getCarryForwardAllowed())) {
                    long pastMonths = Math.max(0, monthsElapsed - 1);
                    allowedCarryForward = Math.min(pastMonths * carryForwardLimit, carriedForward);
                }
            }
            case ANNUAL -> {
                // Pro-rate annual leaves till current month
                totalAccrued = (annualLimit / 12.0) * monthsElapsed;
            }
            case PRO_RATA -> {
                // Optional: handle based on joining date elsewhere
                totalAccrued = annualLimit;
            }
            default -> totalAccrued = 0.0;
        }

        double availableTillNow = totalAccrued + allowedCarryForward - usedSoFar;
        log.debug("Leave balance calc -> emp={}, type={}, months={}, accrued={}, used={}, carry={}, available={}",
                emp.getId(), type.getId(), monthsElapsed, totalAccrued, usedSoFar, allowedCarryForward,
                availableTillNow);

        return Math.max(availableTillNow, 0.0);
    }

    /**
     * Validates if requested days are within available balance and policy limits.
     */
    public void validateLeaveRequest(Employee emp,
            LeaveType type,
            double requestedDays,
            String leaveYear,
            LocalDate leaveYearStart) {

        double available = calculateAvailableBalance(emp, type, leaveYear, leaveYearStart);

        if (requestedDays > available) {
            throw new InsufficientLeaveBalanceException(requestedDays, available);
        }

        if (type.getMaxConsecutiveDays() != null && requestedDays > type.getMaxConsecutiveDays()) {
            throw new InvalidOperationException(
                    "You can apply a maximum of " + type.getMaxConsecutiveDays() + " consecutive days.");
        }
    }

    // You can add a unified helper method in the calculator to compute
    // post-approval balances,
    // so that both validation and deduction happen through it.
    // public void deductApprovedLeave(Employee emp, LeaveType type, double days,
    // String leaveYear, LocalDate leaveYearStart)

}
