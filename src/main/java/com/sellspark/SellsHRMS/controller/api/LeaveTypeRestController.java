package com.sellspark.SellsHRMS.controller.api;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sellspark.SellsHRMS.dto.leave.LeaveTypeRequestDTO;
import com.sellspark.SellsHRMS.dto.leave.LeaveTypeResponseDTO;
import com.sellspark.SellsHRMS.entity.EmployeeLeaveBalance;
import com.sellspark.SellsHRMS.service.EmployeeLeaveBalanceService;
import com.sellspark.SellsHRMS.service.LeaveTypeService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/leave-type")
public class LeaveTypeRestController {
    
    private final LeaveTypeService leaveTypeService;
   

    // create leave type
    @PostMapping("/create")
    public ResponseEntity<LeaveTypeResponseDTO> createLeaveType(@RequestBody LeaveTypeRequestDTO request) {
        return ResponseEntity.ok(leaveTypeService.createLeaveType(request));
    }

    // get all leave types for an org
    @GetMapping("/org/{orgId}")
    public ResponseEntity<List<LeaveTypeResponseDTO>> getAllLeaveTypesByOrg(@PathVariable Long orgId) {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypesByOrg(orgId));
    }

    // get leavetype details by id
    @GetMapping("/{leaveTypeId}")
    public ResponseEntity<LeaveTypeResponseDTO> getLeaveTypeByIdAndOrgId(@PathVariable Long leaveTypeId, @PathVariable Long orgId) {
        return ResponseEntity.ok(leaveTypeService.getLeaveTypeByIdAndOrg(leaveTypeId, orgId));
    }


    @PatchMapping("/{leaveTypeId}/update")
    public ResponseEntity<LeaveTypeResponseDTO> updateLeaveType(@PathVariable Long leaveTypeId, @RequestBody  LeaveTypeRequestDTO req) {
        return ResponseEntity.ok(leaveTypeService.updateLeaveType(leaveTypeId, req));
    }


    @DeleteMapping("/{leaveTypeId}/delete")
    public void delete(@PathVariable Long leaveTypeId){ 
        leaveTypeService.deleteLeaveType(leaveTypeId);
        

    }

}
