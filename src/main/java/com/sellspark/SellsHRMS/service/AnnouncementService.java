package com.sellspark.SellsHRMS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sellspark.SellsHRMS.dto.announcement.AnnouncementRequestDTO;
import com.sellspark.SellsHRMS.dto.announcement.AnnouncementResponseDTO;

@Service
public interface AnnouncementService {

    AnnouncementResponseDTO createAnnouncement(Long orgId, AnnouncementRequestDTO request);
    AnnouncementResponseDTO updateAnnouncement(Long announcementId, Long orgId, AnnouncementRequestDTO request);
     void deleteAnnouncement(Long announcementId, Long orgId);
     AnnouncementResponseDTO getAnnouncementById(Long announcementId, Long orgId);
     List<AnnouncementResponseDTO> getAllAnnouncements(Long orgId);
     List<AnnouncementResponseDTO> getActiveAnnouncements(Long orgId);
     void toggleAnnouncementStatus(Long announcementId, Long orgId);
}