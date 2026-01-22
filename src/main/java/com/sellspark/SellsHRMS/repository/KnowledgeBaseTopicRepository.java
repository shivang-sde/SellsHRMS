package com.sellspark.SellsHRMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.KnowledgeBaseSubject;
import com.sellspark.SellsHRMS.entity.KnowledgeBaseTopic;
import com.sellspark.SellsHRMS.entity.Organisation;


public interface KnowledgeBaseTopicRepository extends JpaRepository<KnowledgeBaseTopic, Long> {
    List<KnowledgeBaseTopic> findBySubjectIdAndIsActiveTrue(Long subjectId);

    List<KnowledgeBaseTopic> findBySubject(KnowledgeBaseSubject subject);
    List<KnowledgeBaseTopic> findBySubjectAndIsActiveTrue(KnowledgeBaseSubject subject);
    List<KnowledgeBaseTopic> findBySubject_OrganisationAndTitleContainingIgnoreCaseAndIsActiveTrue(
    Organisation organisation, String keyword);
}