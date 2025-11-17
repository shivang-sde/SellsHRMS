package com.sellspark.SellsHRMS.superadmin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SuperAdminRepositry extends JpaRepository<SuperAdmin, Long>{

    Optional<SuperAdmin> findByEmail(String email);


    
} 