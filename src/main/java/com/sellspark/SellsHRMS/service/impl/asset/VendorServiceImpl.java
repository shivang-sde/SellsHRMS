package com.sellspark.SellsHRMS.service.impl.asset;

import com.sellspark.SellsHRMS.dto.asset.VendorDTO;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.asset.Vendor;
import com.sellspark.SellsHRMS.mapper.asset.VendorMapper;
import com.sellspark.SellsHRMS.repository.asset.VendorRepository;
import com.sellspark.SellsHRMS.service.asset.VendorService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository repo;
    private final VendorMapper mapper;

    @Override
    public VendorDTO create(VendorDTO dto) {
        Vendor entity = new Vendor();
        entity.setName(dto.getName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setGstNumber(dto.getGstNumber());

        Organisation org = new Organisation();
        org.setId(dto.getOrgId());
        entity.setOrganisation(org);

        return mapper.toDTO(repo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDTO getById(Long id) {
        return mapper.toDTO(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorDTO> getAllByOrgId(Long orgId) {
        return repo.findByOrganisation_Id(orgId).stream()
                .map(mapper::toDTO).toList();
    }

    @Override
    public VendorDTO update(Long id, VendorDTO dto) {
        Vendor existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        if (dto.getName() != null)
            existing.setName(dto.getName());
        if (dto.getContactPerson() != null)
            existing.setContactPerson(dto.getContactPerson());
        if (dto.getEmail() != null)
            existing.setEmail(dto.getEmail());
        if (dto.getPhone() != null)
            existing.setPhone(dto.getPhone());
        if (dto.getAddress() != null)
            existing.setAddress(dto.getAddress());
        if (dto.getGstNumber() != null)
            existing.setGstNumber(dto.getGstNumber());
        return mapper.toDTO(repo.save(existing));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
