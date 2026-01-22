package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.announcement.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
class AnnouncementServiceImpl implements AnnouncementService {

    private final OrganisationAnnouncementRepository announcementRepository;
    private final OrganisationRepository organisationRepository;

    @Override
    public AnnouncementResponseDTO createAnnouncement(Long orgId, AnnouncementRequestDTO request) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        OrganisationAnnouncement announcement = OrganisationAnnouncement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .validUntil(request.getValidUntil())
                .isActive(true)
                .organisation(org)
                .build();

        announcement = announcementRepository.save(announcement);
        return toResponseDTO(announcement);
    }

    @Override
    public AnnouncementResponseDTO updateAnnouncement(Long announcementId, Long orgId, AnnouncementRequestDTO request) {
        OrganisationAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (!announcement.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());
        announcement.setValidUntil(request.getValidUntil());

        announcement = announcementRepository.save(announcement);
        return toResponseDTO(announcement);
    }

    @Override
    public void deleteAnnouncement(Long announcementId, Long orgId) {
        OrganisationAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (!announcement.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        announcementRepository.delete(announcement);
    }

    @Override
    public AnnouncementResponseDTO getAnnouncementById(Long announcementId, Long orgId) {
        OrganisationAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (!announcement.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        return toResponseDTO(announcement);
    }

    @Override
    public List<AnnouncementResponseDTO> getAllAnnouncements(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        return announcementRepository.findByOrganisationOrderByCreatedAtDesc(org).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementResponseDTO> getActiveAnnouncements(Long orgId) {
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        LocalDateTime now = LocalDateTime.now();
        return announcementRepository.findByOrganisationAndIsActiveTrueOrderByCreatedAtDesc(org).stream()
                .filter(a -> a.getValidUntil() == null || a.getValidUntil().isAfter(now))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleAnnouncementStatus(Long announcementId, Long orgId) {
        OrganisationAnnouncement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (!announcement.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access");
        }

        announcement.setIsActive(!announcement.getIsActive());
        announcementRepository.save(announcement);
    }

    private AnnouncementResponseDTO toResponseDTO(OrganisationAnnouncement announcement) {
        return AnnouncementResponseDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .message(announcement.getMessage())
                .validUntil(announcement.getValidUntil())
                .isActive(announcement.getIsActive())
                .organisationId(announcement.getOrganisation().getId())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }
}
