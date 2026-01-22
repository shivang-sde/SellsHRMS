package com.sellspark.SellsHRMS.repository;

import com.sellspark.SellsHRMS.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Optional<Organisation> findByName(String name);

    @Modifying
    @Query("UPDATE Organisation o SET o.isActive = :active, o.suspendedReason = :reason WHERE o.id = :id") 
    void updateStatus(@Param("id") Long id, @Param("active") boolean active, @Param("reason") String reason);

    Optional<Organisation> findByDomain(String domain);

    List<Organisation> findAllByOrderByIdDesc();
    List<Organisation> findByIsActiveTrue();
    List<Organisation> findByValidityBeforeAndIsActiveTrue(LocalDate date);


    @Query("""
    SELECT o, a 
    FROM Organisation o 
    LEFT JOIN OrganisationAdmin a ON a.organisation.id = o.id 
    ORDER BY o.id DESC
""")
List<Object[]> findAllWithAdmins();



}
