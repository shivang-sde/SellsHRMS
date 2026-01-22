package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.OrgAdminDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminCreateDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminSummaryDTO;
import com.sellspark.SellsHRMS.dto.admin.OrgAdminUpdateDTO;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;

public interface OrganisationAdminService {
    OrganisationAdmin findByEmail(String email);


    // OrgAdminSummaryDTO create(OrgAdminCreateDTO dto, Long organisationId);
    OrgAdminSummaryDTO getById(Long id);
    List<OrgAdminSummaryDTO> getAll();
    OrgAdminSummaryDTO update(Long id, OrgAdminUpdateDTO dto);
    void delete(Long id);

    void activateOrgAdmin(Long id);
    void deactivateOrgAdmin(Long id);


    List<OrgAdminSummaryDTO> getByOrganisationId(Long orgId);


    int getEmployeeCount(Long organisationId);

    OrgAdminSummaryDTO patchUpdate(Long id, com.sellspark.SellsHRMS.dto.admin.OrgAdminPatchRequest dto);


}
