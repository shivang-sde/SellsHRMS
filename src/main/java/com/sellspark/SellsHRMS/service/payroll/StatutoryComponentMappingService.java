package com.sellspark.SellsHRMS.service.payroll;

import java.util.List;
import com.sellspark.SellsHRMS.dto.payroll.StatutoryComponentMappingDTO;

public interface StatutoryComponentMappingService {

    StatutoryComponentMappingDTO createMapping(StatutoryComponentMappingDTO dto);

    StatutoryComponentMappingDTO updateMapping(Long id, StatutoryComponentMappingDTO dto);

    void deactivateMapping(Long id);

    StatutoryComponentMappingDTO getMappingById(Long id);

    List<StatutoryComponentMappingDTO> getMappingsByOrganisation(Long organisationId);

    List<StatutoryComponentMappingDTO> getMappingsByStatutoryComponent(Long orgId, Long statutoryComponentId);
}
