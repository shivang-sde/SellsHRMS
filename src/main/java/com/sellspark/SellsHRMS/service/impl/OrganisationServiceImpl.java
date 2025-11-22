package com.sellspark.SellsHRMS.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepo;

    @Override
    public Organisation create(Organisation organisation) {
        return organisationRepo.save(organisation);
    }

    @Override
    public Optional<Organisation> getById(Long id) {
        return organisationRepo.findById(id);
    }

    @Override
    public List<Organisation> getAll() {
        return organisationRepo.findAll();
    }

    @Override
    public Organisation update(Long id, Organisation updated) {
        return organisationRepo.findById(id)
                .map(org -> {
                    org.setName(updated.getName());
                    org.setDomain(updated.getDomain());
                    org.setAddress(updated.getAddress());
                    org.setContactEmail(updated.getContactEmail());
                    org.setContactPhone(updated.getContactPhone());
                    return organisationRepo.save(org);
                })
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
    }

    @Override
    public void delete(Long id) {
        organisationRepo.deleteById(id);
    }
}
