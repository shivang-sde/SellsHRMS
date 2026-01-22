package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.holiday.HolidayRequest;
import com.sellspark.SellsHRMS.dto.holiday.HolidayResponse;
import com.sellspark.SellsHRMS.entity.Holiday;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.DuplicateResourceException;
import com.sellspark.SellsHRMS.exception.HolidayNotFoundException;
import com.sellspark.SellsHRMS.exception.OrganisationNotFoundException;
import com.sellspark.SellsHRMS.exception.UnauthorizedAccessException;
import com.sellspark.SellsHRMS.repository.HolidayRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public HolidayResponse createHoliday(HolidayRequest request) {
        log.info("Creating holiday for org: {}", request.getOrgId());
        
        Organisation org = organisationRepository.findById(request.getOrgId())
                .orElseThrow(() -> new OrganisationNotFoundException(request.getOrgId()));

        // Check if holiday already exists on this date
        if (holidayRepository.existsByOrganisationAndHolidayDate(org, request.getHolidayDate())) {
            throw new RuntimeException("Holiday already exists on this date for this organisation");
        }

        Holiday holiday = Holiday.builder()
                .organisation(org)
                .holidayDate(request.getHolidayDate())
                .holidayName(request.getHolidayName())
                .holidayType(Holiday.HolidayType.valueOf(request.getHolidayType()))
                .isMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : true)
                .description(request.getDescription())
                .build();

        holiday = holidayRepository.save(holiday);
        log.info("Holiday created with ID: {}", holiday.getId());
        
        return toResponseDTO(holiday);
    }

    @Override
    public HolidayResponse updateHoliday(Long holidayId, HolidayRequest request) {
        log.info("Updating holiday ID: {}", holidayId);
        
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new HolidayNotFoundException(holidayId));

        // Verify organisation match
        if (!holiday.getOrganisation().getId().equals(request.getOrgId())) {
            throw new UnauthorizedAccessException();
        }

        // If date is being changed, check for conflicts
        if (!holiday.getHolidayDate().equals(request.getHolidayDate())) {
            if (holidayRepository.existsByOrganisationAndHolidayDate(
                    holiday.getOrganisation(), request.getHolidayDate())) {
                throw new DuplicateResourceException("Holiday", "id", holiday.getId());
            }
            holiday.setHolidayDate(request.getHolidayDate());
        }

        holiday.setHolidayName(request.getHolidayName());
        holiday.setHolidayType(Holiday.HolidayType.valueOf(request.getHolidayType()));
        holiday.setIsMandatory(request.getIsMandatory());
        holiday.setDescription(request.getDescription());

        holiday = holidayRepository.save(holiday);
        log.info("Holiday updated successfully");
        
        return toResponseDTO(holiday);
    }

    @Override
    public void deleteHoliday(Long holidayId, Long orgId) {
        log.info("Deleting holiday ID: {} for org: {}", holidayId, orgId);
        
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new HolidayNotFoundException(holidayId));

        if (!holiday.getOrganisation().getId().equals(orgId)) {
            throw new UnauthorizedAccessException();
        }

        holidayRepository.delete(holiday);
        log.info("Holiday deleted successfully");
    }

    @Override
    public HolidayResponse getHolidayById(Long holidayId, Long orgId) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() ->  new HolidayNotFoundException(orgId));

        if (!holiday.getOrganisation().getId().equals(orgId)) {
            throw new UnauthorizedAccessException();
        }

        return toResponseDTO(holiday);
    }

    @Override
    public List<HolidayResponse> getAllHolidaysByOrg(Long orgId) {
        log.info("Fetching all holidays for org: {}", orgId);
        
        return holidayRepository.findByOrganisationId(orgId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayResponse> getHolidaysByDateRange(Long orgId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching holidays between {} and {} for org: {}", startDate, endDate, orgId);
        
        return holidayRepository.findByOrganisationIdAndHolidayDateBetween(orgId, startDate, endDate).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayResponse> getCurrentYearHolidays(Long orgId) {
        log.info("Fetching current year holidays for org: {}", orgId);
        
        LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
        LocalDate endOfYear = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        
        return getHolidaysByDateRange(orgId, startOfYear, endOfYear);
    }

    @Override
    public List<HolidayResponse> getUpcomingHolidays(Long orgId) {
        log.info("Fetching upcoming holidays for org: {}", orgId);
        
        LocalDate today = LocalDate.now();
        LocalDate endOfYear = today.withDayOfYear(today.lengthOfYear());
        
        List<HolidayResponse> holidays = holidayRepository.findByOrganisationIdAndHolidayDateBetween(orgId, today, endOfYear).stream()
                .map(this::toResponseDTO)
                .sorted((h1, h2) -> h1.getHolidayDate().compareTo(h2.getHolidayDate()))
                .collect(Collectors.toList());
        
                log.info("holidays size {}", holidays.size());
        return holidays;
    }

     @Override
    public List<HolidayResponse> getUpcomingHolidays(Long orgId, LocalDate start, LocalDate end) {
        log.info("Fetching upcoming holidays for org: {}", orgId);
        return holidayRepository.findByOrganisationIdAndHolidayDateBetween(orgId, start, end).stream()
        .map(this::toResponseDTO)
        .sorted((h1, h2)-> h1.getHolidayDate().compareTo(h2.getHolidayDate()))
        .collect(Collectors.toList());
    }

    @Override
    public List<HolidayResponse> getMandatoryHolidays(Long orgId) {
        log.info("Fetching mandatory holidays for org: {}", orgId);
        
        return holidayRepository.findByOrganisationId(orgId).stream()
                .filter(Holiday::getIsMandatory)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayResponse> getHolidaysByType(Long orgId, String holidayType) {
        log.info("Fetching holidays of type {} for org: {}", holidayType, orgId);
        
        Holiday.HolidayType type = Holiday.HolidayType.valueOf(holidayType);
        
        return holidayRepository.findByOrganisationId(orgId).stream()
                .filter(h -> h.getHolidayType().equals(type))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isHoliday(Long orgId, LocalDate date) {
        return holidayRepository.existsByOrganisationIdAndHolidayDate(orgId, date);
    }

    @Override
    public HolidayResponse getHolidayByDate(Long orgId, LocalDate date) {
        return holidayRepository.findByOrganisationIdAndHolidayDate(orgId, date)
                .map(this::toResponseDTO)
                .orElse(null);
    }

    @Override
    public int getHolidayCountForYear(Long orgId, int year) {
        log.info("Counting holidays for year {} in org: {}", year, orgId);
        
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        
        return holidayRepository.findByOrganisationIdAndHolidayDateBetween(orgId, startOfYear, endOfYear).size();
    }

    @Override
    public List<LocalDate> getHolidayDatesForRange(Long orgId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching holiday dates between {} and {} for org: {}", startDate, endDate, orgId);
        
        return holidayRepository.findByOrganisationIdAndHolidayDateBetween(orgId, startDate, endDate).stream()
                .map(Holiday::getHolidayDate)
                .sorted()
                .collect(Collectors.toList());
    }

    // ==================== Helper Method ====================
    
    private HolidayResponse toResponseDTO(Holiday holiday) {
        HolidayResponse response = new HolidayResponse();
        response.setId(holiday.getId());
        response.setOrgId(holiday.getOrganisation().getId());
        response.setHolidayDate(holiday.getHolidayDate());
        response.setHolidayName(holiday.getHolidayName());
        response.setHolidayType(holiday.getHolidayType().toString());
        response.setIsMandatory(holiday.getIsMandatory());
        response.setDescription(holiday.getDescription());
        return response;
    }
}