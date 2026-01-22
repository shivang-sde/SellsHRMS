package com.sellspark.SellsHRMS.exception;

public class LeaveNotFoundException extends ResourceNotFoundException {
    public LeaveNotFoundException(Long id) {
        super("Leave", "id", id);
    }
}