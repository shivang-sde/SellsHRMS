package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.holiday.HolidayRequest;
import com.sellspark.SellsHRMS.dto.holiday.HolidayResponse;
import com.sellspark.SellsHRMS.entity.Holiday;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    
    // Create and Update
    HolidayResponse createHoliday(HolidayRequest request);
    HolidayResponse updateHoliday(Long holidayId, HolidayRequest request);
    
    // Delete
    void deleteHoliday(Long holidayId, Long orgId);
    
    // Read operations
    HolidayResponse getHolidayById(Long holidayId, Long orgId);
    List<HolidayResponse> getAllHolidaysByOrg(Long orgId);
    List<HolidayResponse> getHolidaysByDateRange(Long orgId, LocalDate startDate, LocalDate endDate);
    List<HolidayResponse> getCurrentYearHolidays(Long orgId);
    List<HolidayResponse> getUpcomingHolidays(Long orgId);
    List<HolidayResponse> getMandatoryHolidays(Long orgId);
    List<HolidayResponse> getHolidaysByType(Long orgId, String holidayType);
     List<HolidayResponse> getUpcomingHolidays(Long orgId, LocalDate start, LocalDate end);
    
    // Utility methods
    boolean isHoliday(Long orgId, LocalDate date);
    HolidayResponse getHolidayByDate(Long orgId, LocalDate date);
    int getHolidayCountForYear(Long orgId, int year);
    List<LocalDate> getHolidayDatesForRange(Long orgId, LocalDate startDate, LocalDate endDate);
}
