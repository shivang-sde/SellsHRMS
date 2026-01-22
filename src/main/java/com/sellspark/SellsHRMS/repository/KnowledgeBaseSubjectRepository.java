package com.sellspark.SellsHRMS.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sellspark.SellsHRMS.entity.*;

public interface KnowledgeBaseSubjectRepository extends JpaRepository<KnowledgeBaseSubject, Long> {
    List<KnowledgeBaseSubject> findByOrganisationId(Long orgId);
    List<KnowledgeBaseSubject> findByOrganisation(Organisation organisation);
    List<KnowledgeBaseSubject> findByOrganisationOrderByCreatedAtDesc(Organisation organisation);

}

