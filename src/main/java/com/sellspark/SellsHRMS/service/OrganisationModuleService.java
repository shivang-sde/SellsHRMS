package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.entity.Module;

public interface OrganisationModuleService {

    void assignModuleToOrganisation(Long orgId, List<String> moduleCodes);

    List<Module> getAllModules();

    List<String> getActiveModuleCodes(Long orgId);

    List<String> findActiveModuleCodesByOrganisationId(Long orgId);

}