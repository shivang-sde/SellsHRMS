package com.sellspark.SellsHRMS.repository.auth;

import java.util.Optional;

import com.sellspark.SellsHRMS.entity.auth.OtpToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(String identifier);
}
