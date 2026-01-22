package com.sellspark.SellsHRMS.exception;

public class LeaveTypeNotFoundException extends ResourceNotFoundException {
    public LeaveTypeNotFoundException(Long id) {
        super("Leave Type", "id", id);
    }
}