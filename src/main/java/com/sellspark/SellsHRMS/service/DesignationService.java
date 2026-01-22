package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.dto.organisation.DesignationDTO;
import java.util.List;

public interface DesignationService {

    DesignationDTO createDesignation(DesignationDTO designationDTO);

    DesignationDTO getDesignationById(Long id);

    List<DesignationDTO> getAllDesignationsByOrgId(Long orgId);

    List<DesignationDTO> getDesignationsByDeptId(Long deptId);

    DesignationDTO patchUpdateDesignation(Long id, DesignationDTO partialUpdate);

    void deleteDesignation(Long id);
}
