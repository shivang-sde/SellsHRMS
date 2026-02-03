package com.sellspark.SellsHRMS.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.exception.core.HRMSException;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeCodeGenerator {
    private final OrganisationRepository orgRepo;

    @Transactional
    public synchronized String generateEmployeeCode(Long orgId) {
        Organisation org = orgRepo.findById(orgId)
                .orElseThrow(() -> new HRMSException("Organisation not found", "ORG_NOT_FOUND", HttpStatus.NOT_FOUND));

        Integer nextSeq = (org.getEmpSequence() == null ? 1 : org.getEmpSequence() + 1);
        org.setEmpSequence(nextSeq);
        orgRepo.save(org); // updates latest sequence

        String prefix = org.getEmpPrefix() != null ? org.getEmpPrefix() : "ORG";
        return String.format("%s%04d", prefix.toUpperCase(), nextSeq); // 4-digit padding
    }
}
