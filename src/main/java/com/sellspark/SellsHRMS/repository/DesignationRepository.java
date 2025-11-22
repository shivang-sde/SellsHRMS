package com.sellspark.SellsHRMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sellspark.SellsHRMS.entity.Designation;
import com.sellspark.SellsHRMS.entity.Organisation;

import java.util.List;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByOrganisation(Organisation organisation);

}
