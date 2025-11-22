package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.OrgAdminDTO;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;

public interface OrganisationAdminService {
    OrganisationAdmin findByEmail(String email);

    OrganisationAdmin create(OrgAdminDTO dto);

    // OrganisationAdmin create(String fullName, String email, String password, Long
    // OrgId);

    OrganisationAdmin getById(Long id);

    List<OrganisationAdmin> getByOrganisationId(Long orgId);

    List<OrganisationAdmin> getAll();

    int getEmployeeCount(Long organisationId);

    OrganisationAdmin update(Long id, OrgAdminDTO dto);

    void delete(Long id);

}

// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import com.sellspark.SellsHRMS.dto.EmployeeCreateRequest;
// import com.sellspark.SellsHRMS.dto.OrgUpdateRequest;
// import com.sellspark.SellsHRMS.dto.UserCreateRequest;
// import com.sellspark.SellsHRMS.entity.Department;
// import com.sellspark.SellsHRMS.entity.Designation;
// import com.sellspark.SellsHRMS.entity.Employee;
// import com.sellspark.SellsHRMS.entity.Organisation;
// import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
// import com.sellspark.SellsHRMS.entity.Role;
// import com.sellspark.SellsHRMS.repository.DepartmentRepository;
// import com.sellspark.SellsHRMS.repository.DesignationRepository;
// import com.sellspark.SellsHRMS.repository.EmployeeRepository;
// import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
// import com.sellspark.SellsHRMS.repository.OrganisationRepository;
// import com.sellspark.SellsHRMS.repository.RoleRepository;
// import com.sellspark.SellsHRMS.repository.UserRepository;
// import com.sellspark.SellsHRMS.entity.User;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class OrganisationAdminService {

// private final OrganisationRepository organisationRepo;
// private final OrganisationAdminRepository orgAdminRepo;
// private final EmployeeRepository employeeRepo;
// private final DepartmentRepository departmentRepo;
// private final DesignationRepository designationRepo;
// private final UserRepository userRepo;
// private final RoleRepository roleRepo;
// private final PasswordEncoder encoder;

// public OrganisationAdmin login(String email, String password) {
// return orgAdminRepo.findByEmail(email)
// .filter(admin -> encoder.matches(password, admin.getPasswordHash()))
// .orElse(null);
// }

// public int getEmployeeCount(Long orgId) {
// return employeeRepo.countByOrganisationId(orgId);
// }

// public Organisation updateOrganisation(Long OrgAdminId, OrgUpdateRequest req)
// {

// OrganisationAdmin orgAdmin = orgAdminRepo.findById(OrgAdminId)
// .orElseThrow(() -> new RuntimeException("Admin not found"));

// Organisation org = orgAdmin.getOrganisation();

// org.setName(req.getName());
// org.setContactPhone(req.getContactPhone());
// org.setContactEmail(req.getContactEmail());
// org.setAdress(req.getAddress());
// org.setCountry(req.getCountry());
// org.setDomain(req.getDomain());
// org.setPan(req.getPan());
// org.setTan(req.getTan());
// org.setLogoUrl(req.getLogoUrl());

// return organisationRepo.save(org);
// }

// @SuppressWarnings("null")
// public Employee createEmployee(Long orgAdminId, EmployeeCreateRequest req) {
// OrganisationAdmin orgAdmin = orgAdminRepo.findById(orgAdminId)
// .orElseThrow(() -> new RuntimeException("Admin not found"));

// Organisation org = orgAdmin.getOrganisation();

// employeeRepo.findByEmail(req.getEmail()).ifPresent(e -> {
// new RuntimeException("Employee email already exists");
// });

// String employeeCode = "EMP-" + System.currentTimeMillis();

// Department department = null;

// if (req.getDepartmentId() != null) {
// department = departmentRepo.findById(req.getDepartmentId())
// .orElseThrow(() -> new RuntimeException("Department not found"));
// }

// Designation designation = null;
// if (req.getDesignationId() != null) {
// designation = designationRepo.findById(req.getDesignationId())
// .orElseThrow(() -> new RuntimeException("Designation not found"));
// }

// Employee manager = null;
// if (req.getManagerId() != null) {
// manager = employeeRepo.findById(req.getManagerId())
// .orElseThrow(() -> new RuntimeException("Manger Not Found"));
// }

// Employee emp = Employee.builder()
// .employeeCode(employeeCode)
// .firstName(req.getFirstName())
// .lastName(req.getLastName())
// .email(req.getEmail())
// .phone(req.getPhone())
// .gender(req.getGender())
// .dob(req.getDob())
// .dateOfJoining(req.getDateOfJoining())
// .salary(req.getSalary())
// .address(req.getAddress())
// .city(req.getCity())
// .state(req.getState())
// .country(req.getCountry())
// .pincode(req.getPincode())
// .employmentType(req.getEmploymentType())
// .status(req.getStatus())
// .organisation(org)
// .department(department)
// .designation(designation)
// .manager(manager)
// .build();

// return employeeRepo.save(emp);

// }

// // login account for employees

// public User createUser(Long OrgAdminId, UserCreateRequest req) {
// OrganisationAdmin admin = orgAdminRepo.findById(OrgAdminId)
// .orElseThrow(() -> new RuntimeException("Admin not found"));

// Organisation org = admin.getOrganisation();

// Employee employee = employeeRepo.findById(req.getEmployeeId())
// .orElseThrow(() -> new RuntimeException("Employee not found"));

// if (!employee.getOrganisation().getId().equals(org.getId())) {
// throw new RuntimeException("Employee does not belog to your organisation");
// }

// userRepo.findByEmail(req.getEmail()).ifPresent(u -> {
// throw new RuntimeException("User email already exists");
// });

// Role role = roleRepo.findById(req.getRoleId())
// .orElseThrow(() -> new RuntimeException("Role not found"));

// User user = User.builder()
// .organisation(org)
// .employee(employee)
// .email(req.getEmail())
// .passwordHash(encoder.encode(req.getPassword()))
// .role(role)
// .isActive(true)
// .build();

// return userRepo.save(user);
// }

// }