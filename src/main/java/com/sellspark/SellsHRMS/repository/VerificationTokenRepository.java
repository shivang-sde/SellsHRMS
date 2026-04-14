package com.sellspark.SellsHRMS.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sellspark.SellsHRMS.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByTokenAndUsedFalse(String token);

    Optional<VerificationToken> findByEmailAndUsedFalse(String email);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
