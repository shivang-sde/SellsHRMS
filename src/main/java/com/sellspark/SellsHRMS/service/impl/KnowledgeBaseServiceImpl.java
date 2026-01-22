package com.sellspark.SellsHRMS.service.impl;

import com.sellspark.SellsHRMS.dto.kb.*;
import com.sellspark.SellsHRMS.entity.*;
import com.sellspark.SellsHRMS.repository.*;
import com.sellspark.SellsHRMS.service.KnowledgeBaseService;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseSubjectRepository subjectRepository;
    private final KnowledgeBaseTopicRepository topicRepository;
    private final OrganisationRepository organisationRepository;

    // ==================== Subject Operations ====================
    
    @Override
    public KBSubjectResponseDTO createSubject(Long orgId, KBSubjectRequestDTO request) {
        log.info("Creating subject for org: {}", orgId);
        
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        KnowledgeBaseSubject subject = KnowledgeBaseSubject.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .organisation(org)
                .build();

        subject = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", subject.getId());
        
        return toSubjectResponseDTO(subject);
    }

    @Override
    public KBSubjectResponseDTO updateSubject(Long subjectId, Long orgId, KBSubjectRequestDTO request) {
        log.info("Updating subject ID: {} for org: {}", subjectId, orgId);
        
        KnowledgeBaseSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        subject.setTitle(request.getTitle());
        subject.setDescription(request.getDescription());

        subject = subjectRepository.save(subject);
        return toSubjectResponseDTO(subject);
    }

    @Override
    public void deleteSubject(Long subjectId, Long orgId) {
        log.info("Deleting subject ID: {} for org: {}", subjectId, orgId);
        
        KnowledgeBaseSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        subjectRepository.delete(subject);
        log.info("Subject deleted successfully");
    }

    @Override
    public KBSubjectResponseDTO getSubjectById(Long subjectId, Long orgId) {
        KnowledgeBaseSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        return toSubjectResponseDTO(subject);
    }

    @Override
    public List<KBSubjectResponseDTO> getAllSubjects(Long orgId) {
        log.info("Fetching all subjects for org: {}", orgId);
        
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        return subjectRepository.findByOrganisationOrderByCreatedAtDesc(org).stream()
                .map(this::toSubjectResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KBSubjectWithTopicsDTO getSubjectWithTopics(Long subjectId, Long orgId) {
        log.info("Fetching subject with topics. Subject ID: {}, Org ID: {}", subjectId, orgId);
        
        KnowledgeBaseSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        List<KBTopicResponseDTO> topics = topicRepository.findBySubjectAndIsActiveTrue(subject).stream()
                .map(this::toTopicResponseDTO)
                .collect(Collectors.toList());

        return KBSubjectWithTopicsDTO.builder()
                .id(subject.getId())
                .title(subject.getTitle())
                .description(subject.getDescription())
                .topics(topics)
                .createdAt(subject.getCreatedAt())
                .build();
    }

    // ==================== Topic Operations ====================

    @Override
    public KBTopicResponseDTO createTopic(Long orgId, KBTopicRequestDTO request) {
        log.info("Creating topic for subject: {} in org: {}", request.getSubjectId(), orgId);
        
        KnowledgeBaseSubject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        KnowledgeBaseTopic topic = KnowledgeBaseTopic.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .attachmentUrl(request.getAttachmentUrl())
                .subject(subject)
                .isActive(true)
                .build();

        topic = topicRepository.save(topic);
        log.info("Topic created with ID: {}", topic.getId());
        
        return toTopicResponseDTO(topic);
    }

    @Override
    public KBTopicResponseDTO updateTopic(Long topicId, Long orgId, KBTopicRequestDTO request) {
        log.info("Updating topic ID: {} for org: {}", topicId, orgId);
        
        KnowledgeBaseTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getSubject().getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this topic");
        }

        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setAttachmentUrl(request.getAttachmentUrl());

        topic = topicRepository.save(topic);
        return toTopicResponseDTO(topic);
    }

    @Override
    public void deleteTopic(Long topicId, Long orgId) {
        log.info("Deleting topic ID: {} for org: {}", topicId, orgId);
        
        KnowledgeBaseTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getSubject().getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this topic");
        }

        topicRepository.delete(topic);
        log.info("Topic deleted successfully");
    }

    @Override
    public KBTopicResponseDTO getTopicById(Long topicId, Long orgId) {
        KnowledgeBaseTopic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getSubject().getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this topic");
        }

        return toTopicResponseDTO(topic);
    }

    @Override
    public List<KBTopicResponseDTO> getTopicsBySubject(Long subjectId, Long orgId) {
        log.info("Fetching topics for subject: {}", subjectId);
        
        KnowledgeBaseSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        if (!subject.getOrganisation().getId().equals(orgId)) {
            throw new RuntimeException("Unauthorized access to this subject");
        }

        return topicRepository.findBySubjectAndIsActiveTrue(subject).stream()
                .map(this::toTopicResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<KBTopicResponseDTO> searchTopics(Long orgId, String keyword) {
        log.info("Searching topics with keyword: {} for org: {}", keyword, orgId);
        
        Organisation org = organisationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        return topicRepository.findBySubject_OrganisationAndTitleContainingIgnoreCaseAndIsActiveTrue(org, keyword).stream()
                .map(this::toTopicResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
public List<KBDashboardDTO> getDashboardKnowledgeBase(Long orgId) {
    log.info("Fetching dashboard knowledge base for org: {}", orgId);
    
    Organisation org = organisationRepository.findById(orgId)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));

    List<KnowledgeBaseSubject> subjects = subjectRepository.findByOrganisationOrderByCreatedAtDesc(org);
    
    // Limit to top 5 subjects for dashboard
    return subjects.stream()
            .limit(5)
            .map(this::toDashboardDTO)
            .collect(Collectors.toList());
}

@Override
public List<KBDashboardDTO> getAllSubjectsWithTopics(Long orgId) {
    log.info("Fetching all subjects with topics for org: {}", orgId);
    
    Organisation org = organisationRepository.findById(orgId)
            .orElseThrow(() -> new RuntimeException("Organisation not found"));

    List<KnowledgeBaseSubject> subjects = subjectRepository.findByOrganisationOrderByCreatedAtDesc(org);
    
    return subjects.stream()
            .map(this::toDashboardDTO)
            .collect(Collectors.toList());
}

// ==================== HELPER METHOD ====================
// Add this helper method to KnowledgeBaseServiceImpl.java

private KBDashboardDTO toDashboardDTO(KnowledgeBaseSubject subject) {
    // Get active topics for this subject
    List<KnowledgeBaseTopic> topics = topicRepository.findBySubjectAndIsActiveTrue(subject);
    
    // Get recent topics (limit to 3)
    List<KBTopicSummaryDTO> recentTopics = topics.stream()
            .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
            .limit(3)
            .<KBTopicSummaryDTO>map(topic -> KBTopicSummaryDTO.builder()
                    .id(topic.getId())
                    .title(topic.getTitle())
                    .contentPreview(topic.getContent() != null && topic.getContent().length() > 100 
                            ? topic.getContent().substring(0, 100) + "..." 
                            : topic.getContent())
                    .hasAttachment(topic.getAttachmentUrl() != null && !topic.getAttachmentUrl().isEmpty())
                    .createdAt(topic.getCreatedAt())
                    .build())
            .collect(Collectors.toList());
    
    return KBDashboardDTO.builder()
            .id(subject.getId())
            .title(subject.getTitle())
            .description(subject.getDescription())
            .topicCount(topics.size())
            .createdAt(subject.getCreatedAt())
            .updatedAt(subject.getUpdatedAt())
            .recentTopics(recentTopics)
            .build();
}

    // ==================== Helper Methods ====================

    private KBSubjectResponseDTO toSubjectResponseDTO(KnowledgeBaseSubject subject) {
        return KBSubjectResponseDTO.builder()
                .id(subject.getId())
                .title(subject.getTitle())
                .description(subject.getDescription())
                .organisationId(subject.getOrganisation().getId())
                .topicCount(subject.getTopics() != null ? subject.getTopics().size() : 0)
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }

    private KBTopicResponseDTO toTopicResponseDTO(KnowledgeBaseTopic topic) {
        return KBTopicResponseDTO.builder()
                .id(topic.getId())
                .subjectId(topic.getSubject().getId())
                .subjectTitle(topic.getSubject().getTitle())
                .title(topic.getTitle())
                .content(topic.getContent())
                .attachmentUrl(topic.getAttachmentUrl())
                .isActive(topic.getIsActive())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }
}