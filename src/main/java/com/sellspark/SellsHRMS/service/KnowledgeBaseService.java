package com.sellspark.SellsHRMS.service;

import java.util.List;
import com.sellspark.SellsHRMS.dto.kb.KBDashboardDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectRequestDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectResponseDTO;
import com.sellspark.SellsHRMS.dto.kb.KBSubjectWithTopicsDTO;
import com.sellspark.SellsHRMS.dto.kb.KBTopicRequestDTO;
import com.sellspark.SellsHRMS.dto.kb.KBTopicResponseDTO;

public interface KnowledgeBaseService {
   List<KBDashboardDTO> getDashboardKnowledgeBase(Long orgId);
    List<KBDashboardDTO> getAllSubjectsWithTopics(Long orgId);


      // Subject operations
    KBSubjectResponseDTO createSubject(Long orgId, KBSubjectRequestDTO request);
    KBSubjectResponseDTO updateSubject(Long subjectId, Long orgId, KBSubjectRequestDTO request);
    void deleteSubject(Long subjectId, Long orgId);
    KBSubjectResponseDTO getSubjectById(Long subjectId, Long orgId);
    List<KBSubjectResponseDTO> getAllSubjects(Long orgId);
    KBSubjectWithTopicsDTO getSubjectWithTopics(Long subjectId, Long orgId);
    
    // Topic operations
    KBTopicResponseDTO createTopic(Long orgId, KBTopicRequestDTO request);
    KBTopicResponseDTO updateTopic(Long topicId, Long orgId, KBTopicRequestDTO request);
    void deleteTopic(Long topicId, Long orgId);
    KBTopicResponseDTO getTopicById(Long topicId, Long orgId);
    List<KBTopicResponseDTO> getTopicsBySubject(Long subjectId, Long orgId);
    List<KBTopicResponseDTO> searchTopics(Long orgId, String keyword);
}
