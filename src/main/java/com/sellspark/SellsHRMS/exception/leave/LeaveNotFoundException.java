package com.sellspark.SellsHRMS.exception.leave;

import com.sellspark.SellsHRMS.exception.ResourceNotFoundException;

public class LeaveNotFoundException extends ResourceNotFoundException {
    public LeaveNotFoundException(Long id) {
        super("Leave", "id", id);
    }
}