package com.sellspark.SellsHRMS.exception;

public class DuplicateHolidayException extends DuplicateResourceException {
    public DuplicateHolidayException(String date) {
        super("Holiday", "date", date);
    }
}
