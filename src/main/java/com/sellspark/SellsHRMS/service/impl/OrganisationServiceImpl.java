package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.admin.OrgAdminCreateDTO;
import com.sellspark.SellsHRMS.dto.mapper.DtoMapper;
import com.sellspark.SellsHRMS.dto.organisation.*;
import com.sellspark.SellsHRMS.entity.Organisation;
import com.sellspark.SellsHRMS.entity.OrganisationAdmin;
import com.sellspark.SellsHRMS.repository.OrganisationAdminRepository;
import com.sellspark.SellsHRMS.repository.OrganisationRepository;
import com.sellspark.SellsHRMS.service.OrganisationService;
import com.sellspark.SellsHRMS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import  com.sellspark.SellsHRMS.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrganisationServiceImpl implements OrganisationService {

    private final OrganisationRepository organisationRepo;
    private final OrganisationAdminRepository adminRepo;
    private final DtoMapper mapper;
    private final UserService userService;

    @Override
    public OrganisationDTO create(OrganisationDTO dto) {
        // 1) create organisation
        Organisation org = mapper.toOrganisationEntity(dto);
        organisationRepo.save(org);

        // 2) if admin present, create user account + admin entity
        if (dto.getAdminEmail() != null &&  dto.getAdminPassword() != null && dto.getAdminFullName() != null) {

            // create user account via UserService
            User user = userService.createUser(dto.getAdminEmail(), dto.getAdminPassword(), "ORG_ADMIN", "ORG_ADMIN", org.getId());

            if(user == null){
                log.info("user is not created,see the issue in userservice impl.");
            }


            // create admin entity
            OrganisationAdmin adminEntity = mapper.toAdminEntity(dto);
            adminEntity.setOrganisation(org);
            adminRepo.save(adminEntity);

            // link back
            org.setOrgAdmin(adminEntity);
            organisationRepo.save(org);
        }

        return toDto(org);
    }


    @Override
    public OrganisationDTO getOrganisationById(Long id) {
        return organisationRepo.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));
    }


    @Override
    public OrganisationDTO updateOrganisation(Long id, OrganisationDTO dto) {
        Organisation org = organisationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));

        org.setName(dto.getName());
        org.setDomain(dto.getDomain());
        org.setContactEmail(dto.getContactEmail());
        org.setContactPhone(dto.getContactPhone());
        org.setAddress(dto.getAddress());
        org.setCountry(dto.getCountry());
        org.setLogoUrl(dto.getLogoUrl());
        org.setPan(dto.getPan());
        org.setTan(dto.getTan());
        org.setValidity(dto.getValidity());
        org.setMaxEmployees(dto.getMaxEmployees());

        organisationRepo.save(org);
        return toDto(org);
    }

    @Override
    public OrganisationDTO toggleStatus(Long id, boolean activate) {
        Organisation org = organisationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));
        org.setIsActive(activate);
        org.setSuspendedReason(activate ? null : "Revoked by SuperAdmin");
        organisationRepo.save(org);
        return toDto(org);
    }

    @Override
    public OrganisationDTO extendValidity(Long id, LocalDate newValidity) {
        Organisation org = organisationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));
        org.setValidity(newValidity);
        organisationRepo.save(org);
        return toDto(org);
    }

    @Override
    public OrganisationDTO increaseMaxEmployees(Long id, Integer newLimit) {
        Organisation org = organisationRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));
        org.setMaxEmployees(newLimit);
        organisationRepo.save(org);
        return toDto(org);
    }


    @Override
    @Transactional(readOnly = true)
    public OrganisationDTO getById(Long id) {
        Organisation org = organisationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));
        return toDto(org);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, boolean status, String reasone) {
        organisationRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Organisation not found"));
        organisationRepo.updateStatus(id, status, reasone);
    }

    


    @Override
    @Transactional(readOnly = true)
    public List<OrganisationDTO> getAll() {
        return organisationRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }


    @Override
    public List<OrganisationDTO> getAllOrganisations() {

        return organisationRepo.findAllByOrderByIdDesc()
            .stream()
            .map(this::toDto)
            .toList();
    }


    @Override
    public List<OrganisationDTO> getAllOrganisationsWithAdmins() {
    return organisationRepo.findAllWithAdmins()
        .stream()
        .map(row -> {
            Organisation org = (Organisation) row[0];
            OrganisationAdmin admin = (OrganisationAdmin) row[1];
            return toDto(org, admin);
        })
        .toList();
    }



    @Override
    public void delete(Long id) {
        organisationRepo.deleteById(id);
    }


    private OrganisationDTO toDto(Organisation org, OrganisationAdmin admin) {
    return OrganisationDTO.builder()
            .id(org.getId())
            .name(org.getName())
            .domain(org.getDomain())
            .contactEmail(org.getContactEmail())
            .contactPhone(org.getContactPhone())
            .address(org.getAddress())
            .country(org.getCountry())
            .logoUrl(org.getLogoUrl())
            .pan(org.getPan())
            .tan(org.getTan())
            .maxEmployees(org.getMaxEmployees())
            .isActive(org.getIsActive())
            .validity(org.getValidity())
            .suspendedReason(org.getSuspendedReason())
            .adminFullName(admin != null ? admin.getFullName() : "NA")
            .adminEmail(admin != null ? admin.getEmail() : "NA")
            .build();
}




    private OrganisationDTO toDto(Organisation org) {
       OrganisationAdmin admin = adminRepo.findByOrganisation_Id(org.getId());
       
        return OrganisationDTO.builder()
                .id(org.getId())
                .name(org.getName())
                .domain(org.getDomain())
                .contactEmail(org.getContactEmail())
                .contactPhone(org.getContactPhone())
                .address(org.getAddress())
                .country(org.getCountry())
                .logoUrl(org.getLogoUrl())
                .pan(org.getPan())
                .tan(org.getTan())
                .maxEmployees(org.getMaxEmployees())
                .isActive(org.getIsActive())
                .validity(org.getValidity())
                .suspendedReason(org.getSuspendedReason())
                .adminFullName(admin != null ? admin.getFullName() : "NA")
                .adminEmail(admin != null ? admin.getEmail() : "NA")
                .build();
    }
}
