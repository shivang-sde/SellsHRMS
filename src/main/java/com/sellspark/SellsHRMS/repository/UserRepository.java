package com.sellspark.SellsHRMS.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sellspark.SellsHRMS.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);

}
