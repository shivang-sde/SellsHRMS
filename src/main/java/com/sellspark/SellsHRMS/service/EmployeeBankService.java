package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.employee.EmployeeBankRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeBankResponse;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.entity.EmployeeBank;
import com.sellspark.SellsHRMS.repository.EmployeeBankRepository;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeBankService {

    private final EmployeeBankRepository bankRepo;
    private final EmployeeRepository employeeRepo;

    // CREATE
    public EmployeeBankResponse create(EmployeeBankRequest req) {

        Employee employee = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Ensure only one primary account
        if (Boolean.TRUE.equals(req.getIsPrimaryAccount())) {
            if (bankRepo.existsByEmployeeIdAndIsPrimaryAccountTrue(req.getEmployeeId())) {
                throw new RuntimeException("Primary account already exists for employee");
            }
        }

        EmployeeBank bank = EmployeeBank.builder()
                .employee(employee)
                .bankName(req.getBankName())
                .accountNumber(req.getAccountNumber())
                .ifscCode(req.getIfscCode())
                .branch(req.getBranch())
                .isPrimaryAccount(req.getIsPrimaryAccount())
                .build();

        bankRepo.save(bank);
        return toDTO(bank);
    }

    // GET ALL BY EMPLOYEE
    public List<EmployeeBankResponse> getByEmployee(Long employeeId) {
        return bankRepo.findByEmployeeId(employeeId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public EmployeeBankResponse update(Long bankId, EmployeeBankRequest req) {
        EmployeeBank bank = bankRepo.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank record not found"));

        bank.setBankName(req.getBankName());
        bank.setAccountNumber(req.getAccountNumber());
        bank.setIfscCode(req.getIfscCode());
        bank.setBranch(req.getBranch());

        // Primary account change
        if (Boolean.TRUE.equals(req.getIsPrimaryAccount())) {
            bankRepo.findByEmployeeId(bank.getEmployee().getId())
                    .forEach(b -> b.setIsPrimaryAccount(false));
            bank.setIsPrimaryAccount(true);
        }

        bankRepo.save(bank);
        return toDTO(bank);
    }

    // DELETE
    public void delete(Long bankId) {
        bankRepo.deleteById(bankId);
    }

    private EmployeeBankResponse toDTO(EmployeeBank b) {
        return EmployeeBankResponse.builder()
                .id(b.getId())
                .bankName(b.getBankName())
                .accountNumber(b.getAccountNumber())
                .ifscCode(b.getIfscCode())
                .branch(b.getBranch())
                .isPrimaryAccount(b.getIsPrimaryAccount())
                .build();
    }

    // ===================== Helpers ======================

    // private void resetPrimaryAccount(Long employeeId) {
    //     List<EmployeeBank> list = bankRepo.findByEmployeeIdAndIsPrimaryAccountTrue(employeeId);
    //     for (EmployeeBank b : list) {
    //         b.setIsPrimaryAccount(false);
    //         bankRepo.save(b);
    //     }
    // }

    private EmployeeBankResponse mapToResponse(EmployeeBank bank) {
        EmployeeBankResponse res = new EmployeeBankResponse();

        res.setId(bank.getId());
        res.setEmployeeId(bank.getEmployee().getId());
        res.setEmployeeName(bank.getEmployee().getFirstName() + " " + bank.getEmployee().getLastName());

        res.setBankName(bank.getBankName());
        res.setAccountNumber(bank.getAccountNumber());
        res.setIfscCode(bank.getIfscCode());
        res.setBranch(bank.getBranch());
        res.setIsPrimaryAccount(bank.getIsPrimaryAccount());

        return res;
    }
}
