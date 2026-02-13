package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalaryStructureDTO;
import com.sellspark.SellsHRMS.dto.payroll.StructureUpdatePreviewDTO;

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

    StructureUpdatePreviewDTO previewImpact(Long structureId, List<Long> newComponentIds);

    /**
     * Versioned Update: If the structure is in use, creates a new version and
     * migrates active employees.
     */
    SalaryStructureDTO updateStructureWithVersion(Long id, SalaryStructureDTO dto);
}
