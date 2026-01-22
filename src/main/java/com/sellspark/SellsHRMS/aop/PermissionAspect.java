// package com.sellspark.SellsHRMS.aop;

// import java.nio.file.AccessDeniedException;
// import java.util.Set;

// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.springframework.stereotype.Component;

// import com.sellspark.SellsHRMS.annotation.PermissionRequired;
// import com.sellspark.SellsHRMS.entity.Employee;
// import com.sellspark.SellsHRMS.entity.User;
// import com.sellspark.SellsHRMS.repository.RoleRepository;
// import com.sellspark.SellsHRMS.service.UserService;

// import lombok.RequiredArgsConstructor;

// @Aspect
// @Component
// @RequiredArgsConstructor
// public class PermissionAspect {

//     private final UserService userService;  // to get current user
//     private final RoleRepository rolePermissionRepo;

//     @Before("@annotation(permissionRequired)")
//     public void checkPermission(PermissionRequired permissionRequired) {
//         String requiredCode = permissionRequired.value();

//         User user = userService.getCurrentUser();
//         if (user == null || !user.getIsActive()) {
//             throw new AccessDeniedException("User not logged in or inactive");
//         }

//         // SUPER_ADMIN bypass
//         if (user.getRole() != null && "SUPER_ADMIN".equalsIgnoreCase(user.getRole().getName())) {
//             return;
//         }

//         Employee emp = user.getEmployee();
//         if (emp == null) {
//             throw new AccessDeniedException("User is not an employee");
//         }

//         Set<String> permissionCodes = rolePermissionRepo.findPermissionCodesByRoleId(emp.getRole().getId());

//         if (!permissionCodes.contains(requiredCode)) {
//             throw new AccessDeniedException("You do not have permission: " + requiredCode);
//         }
//     }
// }

