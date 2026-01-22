package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryStructureDTO;
import java.util.List;

public interface SalaryStructureService {

    SalaryStructureDTO createStructure(SalaryStructureDTO dto);

    SalaryStructureDTO updateStructure(Long id, SalaryStructureDTO dto);

    void deactivateStructure(Long id);

    SalaryStructureDTO getStructure(Long id);

    List<SalaryStructureDTO> getAllStructures(Long organisationId);

    /**
     * Assign or update list of components in a salary structure.
     */
    SalaryStructureDTO assignComponents(Long structureId, List<Long> componentIds);

    /**
     * Duplicate an existing structure (for quick versioning or reuse).
     */
    SalaryStructureDTO cloneStructure(Long structureId, String newName);
}
