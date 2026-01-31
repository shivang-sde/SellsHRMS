package com.sellspark.SellsHRMS.exception.leave;

import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

public class LeaveTypeNotFoundException extends ResourceNotFoundException {
    public LeaveTypeNotFoundException(Long id) {
        super("Leave Type", "id", id);
    }
}