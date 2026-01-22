package com.sellspark.SellsHRMS.exception;

public class HolidayNotFoundException extends ResourceNotFoundException {
    public HolidayNotFoundException(Long id) {
        super("Holiday", "id", id);
    }
}