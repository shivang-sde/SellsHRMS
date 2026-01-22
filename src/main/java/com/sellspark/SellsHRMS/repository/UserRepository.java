package com.sellspark.SellsHRMS.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);

     @Query("SELECT u FROM User u JOIN FETCH u.organisation WHERE u.email = :email")
    Optional<User> findByEmailWithOrganisation(@Param("email") String email);

}
