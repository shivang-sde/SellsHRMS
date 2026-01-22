package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.employee.AddressDTO;
import com.sellspark.SellsHRMS.dto.employee.EmployeeCreateRequest;
import com.sellspark.SellsHRMS.dto.employee.EmployeeDetailResponse;
import com.sellspark.SellsHRMS.dto.employee.EmployeeResponse;

import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.entity.Employee.EmployeeStatus;
import com.sellspark.SellsHRMS.entity.Employee.EmploymentType;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.exception.HRMSException;
import com.sellspark.SellsHRMS.exception.InvalidOperationException;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;
import com.sellspark.SellsHRMS.repository.*;

import com.sellspark.SellsHRMS.service.EmployeeService;
import com.sellspark.SellsHRMS.service.LeaveService;
import com.sellspark.SellsHRMS.service.UserService;
import com.sellspark.SellsHRMS.utils.EmployeeHierarchyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final UserService userService;
    private final EmployeeRepository employeeRepo;
    private final OrganisationRepository orgRepo;
    private final DepartmentRepository deptRepo;
    private final DesignationRepository desigRepo;
    private final RoleRepository roleRepo;
    private final LeaveService leaveService;
    private final EmployeeHierarchyUtil employeeHierarchyUtil;

  

    @Override
    public EmployeeResponse create(EmployeeCreateRequest req) {
        
        Long orgId = req.getOrganisationId();
        Organisation org = orgRepo.findById(orgId)
            .orElseThrow(() -> new HRMSException(
                    "Organisation not found.",
                    "ORG_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            ));

            // check emp limit 
        long currentCount = employeeRepo.countByOrganisationIdAndDeletedFalse(orgId);

        if (org.getMaxEmployees() != null && currentCount >= org.getMaxEmployees()) {
            throw new HRMSException(
                "Employee creation limit exceeded for organisation '" + org.getName() + "'. " +
                "Please request to increase max employee limit.",
                "ORG_EMPLOYEE_LIMIT_EXCEEDED",
                HttpStatus.FORBIDDEN
            );
        }

        Designation desig = desigRepo.findById(req.getDesignationId())
            .orElseThrow(() -> 
                new ResourceNotFoundException("Designation", "id ", req.getDesignationId())
            );


    Role orgRole = roleRepo.findById(desig.getRole().getId()).orElseThrow(() -> 
     new HRMSException("Role not found with Id " + desig.getRole().getId() + ". " + "Please create a role first", "ROLE_NOT_FOUND", HttpStatus.NOT_FOUND ));


        Employee emp = mapRequestToEntity(new Employee(), req);
        employeeRepo.save(emp);

        userService.createEmpUser(
                emp.getId(),
                req.getWorkEmail(),
                req.getPassword(),
                orgRole.getName(), // system role for employees in all org it is not org dependednt instead it is plateform dependednt and fix. 
                req.getOrganisationId()
        );

        String ly = leaveService.getCurrentLeaveYear(req.getOrganisationId());
        leaveService.initializeLeaveBalancesForEmployee(emp.getId(), emp.getOrganisation().getId(), ly);


        return mapToResponse(emp);
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeCreateRequest req) {
        Employee emp = employeeRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        mapRequestToEntity(emp, req);
        employeeRepo.save(emp);

        return mapToResponse(emp);
    }

    @Override
    public List<EmployeeResponse> getAll(Long orgId) {
        List<Employee> employess = employeeRepo.findByOrganisationIdAndDeletedFalse(orgId);
        return employess.stream()
                .map(this::mapToResponse)
                .toList(); 
    }

    @Override
    public EmployeeDetailResponse getById(Long id) {
        Employee target = employeeRepo.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));
        
        return mapToDetailResponse(target);
    }


    public EmployeeDetailResponse getByIdAndOrg(Long id, Long orgId) {
    var emp = employeeRepo.findByIdAndOrganisationId(id, orgId).orElse(null);
    if (emp == null) return null;
    return mapToDetailResponse(emp);
    } @Override
    public List<EmployeeResponse> findUpcomingBirthdays(Long orgId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching upcoming birthdays for orgId={} from {} to {}", orgId, startDate, endDate);
        List<Employee> employees = employeeRepo.findUpcomingBirthdays(orgId, startDate, endDate);
        return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponse> findUpcomingWorkAnniversaries(Long orgId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching upcoming anniversaries for orgId={} from {} to {}", orgId, startDate, endDate);
        List<Employee> employees = employeeRepo.findUpcomingWorkAnniversaries(orgId, startDate, endDate);
        return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<EmployeeResponse> getSubordinates(Long managerId, Long organisationId) {
    Set<Long> subordinateIds = employeeHierarchyUtil.getAllSubordinateIds(managerId);
    List<Employee> employees = employeeRepo.findAllById(subordinateIds);
    return employees.stream().map(this::mapToResponse).collect(Collectors.toList());
}





    // @Override
    // public Employee getDetailEmpById(Long id) {
    //     Employee emp = employeeRepo.findByIdAndDeletedFalse(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    //     return emp;
    // }

    @Override
    public void softDelete(Long id) {
        Employee emp = employeeRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        emp.setDeleted(true);
        emp.setStatus(EmployeeStatus.INACTIVE);
        employeeRepo.save(emp);
    }

   @Override
public EmployeeResponse updateStatus(Long id, String status) {

    Employee emp = employeeRepo.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    EmployeeStatus newStatus = Arrays.stream(EmployeeStatus.values())
            .filter(s -> s.name().equalsIgnoreCase(status))
            .findFirst()
            .orElseThrow(() -> new InvalidOperationException("Invalid employee status: " + status));

    emp.setStatus(newStatus);
    employeeRepo.save(emp);

    return mapToResponse(emp);
}


    // ================ Mapping Layer =================

    private Employee mapRequestToEntity(Employee emp, EmployeeCreateRequest req) {

        // Basic
        emp.setEmployeeCode(req.getEmployeeCode());
        emp.setFirstName(req.getFirstName());
        emp.setLastName(req.getLastName());
        emp.setEmail(req.getWorkEmail());
        emp.setPhone(req.getPhone());
        emp.setDob(req.getDob());

   
        emp.setGender(Employee.Gender.valueOf(req.getGender()));
        emp.setEmploymentType(EmploymentType.valueOf(req.getEmploymentType()));
        emp.setStatus(EmployeeStatus.valueOf(req.getStatus()));


   
        emp.setDateOfJoining(req.getDateOfJoining());
        emp.setDateOfExit(req.getDateOfExit());

        // Personal
        emp.setFatherName(req.getFatherName());
        emp.setPersonalEmail(req.getPersonalEmail());
        emp.setAlternatePhone(req.getAlternatePhone());
        emp.setNationality(req.getNationality());
        emp.setMaritalStatus(req.getMaritalStatus());
        emp.setReferenceName(req.getReferenceName());
        emp.setReferencePhone(req.getReferencePhone());

        // Photo upload will be implemented separately
        // emp.setPhotoUrl(...);

        // Address
        if (req.getLocalAddress() != null)
            emp.setLocalAddress(mapAddress(req.getLocalAddress()));

        if (req.getPermanentAddress() != null)
            emp.setPermanentAddress(mapAddress(req.getPermanentAddress()));

        // Organisation
        emp.setOrganisation(
                orgRepo.findById(req.getOrganisationId())
                        .orElseThrow(() -> new OrganisationNotFoundException(req.getOrganisationId()))
        );

        // Department
        if (req.getDepartmentId() != null)
            emp.setDepartment(
                    deptRepo.findById(req.getDepartmentId()).orElse(null)
            );

        // Designation
        if (req.getDesignationId() != null)
            emp.setDesignation(
                    desigRepo.findById(req.getDesignationId()).orElse(null)
            );

        // Reporting To
        if (req.getReportingToId() != null)
            emp.setReportingTo(
                    employeeRepo.findById(req.getReportingToId()).orElse(null)
            );



//         if (req.getDataVisibility() != null) {
//     emp.setDataVisibility(Employee.DataVisibility.valueOf(req.getDataVisibility().toUpperCase()));
// } else if (emp.getDataVisibility() == null) {
//     emp.setDataVisibility(Employee.DataVisibility.SELF);
// }


        // Salary (quick field)
        // emp.setSalary(req);

        // shiftId will be handled in EmployeeShift module

        return emp;
    }

    private Address mapAddress(AddressDTO dto) {
        return Address.builder()
                .line1(dto.getAddressLine1())
                .line2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .pincode(dto.getPincode())
                .build();
    }

    private EmployeeResponse mapToResponse(Employee emp) {

        EmployeeResponse res = new EmployeeResponse();

        res.setId(emp.getId());
        res.setEmployeeCode(emp.getEmployeeCode());
        res.setFullName(emp.getFirstName() + " " + emp.getLastName());
        res.setEmail(emp.getEmail());
        res.setPhone(emp.getPhone());

        if (emp.getStatus() != null) {
        res.setStatus(emp.getStatus().name());
    }
        if (emp.getEmploymentType() != null) {
        res.setEmploymentType(emp.getEmploymentType().name());
    }

        if (emp.getOrganisation() != null) {
        res.setOrganisation(emp.getOrganisation().getName());
    }

        if (emp.getDepartment() != null)
            res.setDepartment(emp.getDepartment().getName());

        if (emp.getDesignation() != null)
            res.setDesignation(emp.getDesignation().getTitle());

         if (emp.getDesignation().getRole() != null){
            res.setRole(emp.getDesignation().getRole().getName());
        }



        return res;
    }


    private EmployeeDetailResponse mapToDetailResponse(Employee emp) {

    EmployeeDetailResponse res = new EmployeeDetailResponse();

    res.setId(emp.getId());
    res.setEmployeeCode(emp.getEmployeeCode());
    res.setFirstName(emp.getFirstName());
    res.setLastName(emp.getLastName());
    res.setFullName(emp.getFirstName() + " " + emp.getLastName());
    res.setEmail(emp.getEmail());
    res.setPersonalEmail(emp.getPersonalEmail());
    res.setPhone(emp.getPhone());
    res.setFatherName(emp.getFatherName());
    res.setMaritalStatus(emp.getMaritalStatus());
    res.setNationality(emp.getNationality());


    if(emp.getAlternatePhone() != null) {
        res.setAlternatePhone(emp.getAlternatePhone());
    }

    if(emp.getReferenceName() != null) res.setReferenceName(emp.getReferenceName());

    if(emp.getReferencePhone() != null) res.setReferencePhone(emp.getReferencePhone());

    res.setStatus(emp.getStatus().name());
    res.setEmploymentType(emp.getEmploymentType().name());
    res.setGender(emp.getGender().name());

    if (emp.getDob() != null)
        res.setDob(emp.getDob().toString());

    if(emp.getDateOfJoining() != null) {
        res.setDateOfJoining(emp.getDateOfJoining().toString());
    }

    if(emp.getDateOfExit() != null) {
        res.setDateOfExit(emp.getDateOfExit().toString());
    }

    if (emp.getDepartment() != null)
        res.setDepartment(emp.getDepartment().getName());

    if (emp.getDesignation() != null)
        res.setDesignation(emp.getDesignation().getTitle());

    if (emp.getDesignation().getRole() != null)
     res.setRole(emp.getDesignation().getRole().getName());


    res.setOrganisation(emp.getOrganisation().getName());


    if (emp.getReportingTo() != null)
        res.setReportingToName(emp.getReportingTo().getFirstName() + " " + emp.getReportingTo().getLastName() + " " + "( " + emp.getReportingTo().getEmployeeCode() + " )");

    if (emp.getLocalAddress() != null)
        res.setLocalAddress(mapAddressToDTO(emp.getLocalAddress()));

    if (emp.getPermanentAddress() != null)
        res.setPermanentAddress(mapAddressToDTO(emp.getPermanentAddress()));

    return res;
}

private AddressDTO mapAddressToDTO(Address a) {
    AddressDTO dto = new AddressDTO();
    dto.setAddressLine1(a.getLine1());
    dto.setAddressLine2(a.getLine2());
    dto.setCity(a.getCity());
    dto.setState(a.getState());
    dto.setCountry(a.getCountry());
    dto.setPincode(a.getPincode());
    return dto;
}

}
