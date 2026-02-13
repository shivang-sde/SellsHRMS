package com.sellspark.SellsHRMS.mapper;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlip;
import com.sellspark.SellsHRMS.entity.payroll.SalarySlipComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SalarySlipMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", expression = "java(slip.getEmployee().getFirstName() + \" \" + slip.getEmployee().getLastName())")
    @Mapping(target = "employeeCode", source = "employee.employeeCode")
    @Mapping(target = "panNumber", source = "employee.panNumber")
    @Mapping(target = "uanNumber", source = "employee.uanNumber")

    // Department & Designation
    @Mapping(target = "departmentName", source = "employee.department.name")
    @Mapping(target = "designationName", source = "employee.designation.title")
    @Mapping(target = "departmentId", source = "employee.department.id")
    @Mapping(target = "designationId", source = "employee.designation.id")

    // Pay Period Info
    @Mapping(target = "payMonth", expression = "java(java.time.Month.of(slip.getPayRun().getMonth()).name())")
    @Mapping(target = "payYear", source = "payRun.year")
    @Mapping(target = "targetGross", source = "assignment.monthlyGrossTarget")
    @Mapping(target = "statutoryContributionOrg", source = "statutoryContributionOrg")
    @Mapping(target = "actualGross", source = "grossPay")
    @Mapping(target = "netPayInWords", ignore = true)

    // Assignment info
    @Mapping(target = "assignmentId", source = "assignment.id")
    @Mapping(target = "basePay", source = "assignment.basePay")
    @Mapping(target = "monthlyGrossTarget", source = "assignment.monthlyGrossTarget")
    @Mapping(target = "monthlyNetTarget", source = "assignment.monthlyNetTarget")
    @Mapping(target = "annualCtc", source = "assignment.annualCtc")
    @Mapping(target = "targetBreakdownJson", source = "assignment.targetBreakdownJson")

    // Bank Details
    @Mapping(target = "bankName", source = "employee.primaryBankAccount.bankName")
    @Mapping(target = "bankBranch", source = "employee.primaryBankAccount.branch")
    @Mapping(target = "bankAccountNumber", source = "employee.primaryBankAccount.accountNumber")
    @Mapping(target = "bankIfscCode", source = "employee.primaryBankAccount.ifscCode")

    // PayRun & Components
    @Mapping(target = "payRunId", source = "payRun.id")
    @Mapping(target = "components", source = "components", qualifiedByName = "mapSlipComponents")
    SalarySlipDTO toDTO(SalarySlip slip);

    @Named("mapSlipComponents")
    default List<SalarySlipComponentDTO> mapSlipComponents(List<SalarySlipComponent> components) {
        if (components == null)
            return List.of();
        return components.stream()
                .map(this::toComponentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Internal mapping for individual components to handle logic for
     * regular components vs statutory components.
     */
    default SalarySlipComponentDTO toComponentDTO(SalarySlipComponent c) {
        if (c == null)
            return null;

        Long compId = c.getId();
        String compName = c.getComponentName();
        String compType = c.getComponentType();
        String abbr = c.getComponentAbbreviation();

        // If linked to a SalaryStructure component
        if (c.getComponent() != null) {
            compId = c.getComponent().getId();
            if (compName == null)
                compName = c.getComponent().getName();
            if (compType == null)
                compType = c.getComponent().getType().name();
            if (abbr == null)
                abbr = c.getComponent().getAbbreviation();
        }
        // If linked to a Statutory setup
        else if (c.getStatutoryComponent() != null) {
            compId = c.getStatutoryComponent().getId();
            compName = c.getStatutoryComponent().getName();
        }

        return SalarySlipComponentDTO.builder()
                .id(c.getId())
                .componentId(compId)
                .componentName(compName)
                .componentAbbreviation(abbr)
                .componentType(compType)
                .amount(c.getAmount())
                .isStatutory(c.getIsStatutory())
                .calculationLog(c.getCalculationLog())
                .build();
    }
}