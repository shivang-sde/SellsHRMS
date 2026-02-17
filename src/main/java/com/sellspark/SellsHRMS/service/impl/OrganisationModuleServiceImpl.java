package com.sellspark.SellsHRMS.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.entity.Module;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationModule;
import com.sellspark.SellsHRMS.exception.core.HRMSException;
import com.sellspark.SellsHRMS.repository.ModuleRepository;
import com.sellspark.SellsHRMS.repository.OrganisationModuleRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationModuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganisationModuleServiceImpl implements OrganisationModuleService {

    private final OrganisationRepository organisationRepository;
    private final OrganisationModuleRepository organisationModuleRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    @Override
    public void assignModuleToOrganisation(Long orgId, List<String> moduleCodes) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new HRMSException("Organisation not found", "ORG_NOT_FOUND", HttpStatus.NOT_FOUND));

        List<Module> modules = moduleRepository.findAll();

        // Enable selected ones
        for (Module module : modules) {
            boolean shouldEnable = moduleCodes.contains(module.getCode());

            OrganisationModule orgModule = organisationModuleRepository.findByOrganisationAndModule(org, module)
                    .orElse(null);

            if (orgModule == null && shouldEnable) {
                organisationModuleRepository.save(OrganisationModule.builder()
                        .organisation(org)
                        .module(module)
                        .enabled(true)
                        .assignedAt(LocalDateTime.now())
                        .build());
            } else if (orgModule != null) {
                orgModule.setEnabled(shouldEnable);
                organisationModuleRepository.save(orgModule);
            }
        }

    }

    @Override
    public List<Module> getAllModules() {
        return moduleRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<String> getActiveModuleCodes(Long orgId) {
        return organisationModuleRepository.findActiveModuleCodesByOrganisationId(orgId);
    }

    @Override
    public List<String> findActiveModuleCodesByOrganisationId(Long orgId) {
        return organisationModuleRepository.findActiveModuleCodesByOrganisationId(orgId);
    }

}
