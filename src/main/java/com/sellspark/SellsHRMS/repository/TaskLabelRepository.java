package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskLabelRepository extends JpaRepository<TaskLabel, Long> {

    List<TaskLabel> findByOrganisationIdAndNameIgnoreCase(Long organisationId, String name);

    Optional<TaskLabel> findByNameIgnoreCase(String name);

    @Query("SELECT l FROM TaskLabel l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND l.organisation.id = :orgId")
    List<TaskLabel> searchLabels(@Param("orgId") Long orgId, @Param("keyword") String keyword);
}
