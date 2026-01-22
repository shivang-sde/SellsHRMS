package com.sellspark.SellsHRMS.service;

import java.time.LocalDate;
import java.util.List;

import com.sellspark.SellsHRMS.dto.organisation.*;

public interface OrganisationService {
    OrganisationDTO create(OrganisationDTO dto);
    OrganisationDTO getById(Long id);
    List<OrganisationDTO> getAll();



    // new 
    OrganisationDTO getOrganisationById(Long id);
    List<OrganisationDTO> getAllOrganisations();
    List<OrganisationDTO> getAllOrganisationsWithAdmins() ;
    OrganisationDTO extendValidity(Long id, LocalDate newValidity);
    OrganisationDTO increaseMaxEmployees(Long id, Integer newLimit);
    OrganisationDTO updateOrganisation(Long id, OrganisationDTO dto);
    OrganisationDTO toggleStatus(Long id, boolean activate);
    void updateStatus(Long id, boolean status, String reason);
    void delete(Long id);
}
