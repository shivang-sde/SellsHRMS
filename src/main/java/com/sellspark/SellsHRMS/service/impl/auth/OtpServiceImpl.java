package com.sellspark.SellsHRMS.service.impl.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sellspark.SellsHRMS.entity.auth.OtpToken;
import com.sellspark.SellsHRMS.exception.core.HRMSException;
import com.sellspark.SellsHRMS.repository.auth.OtpTokenRepository;
import com.sellspark.SellsHRMS.service.auth.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;

    @Override
    public String generateOtp(String identifier) {
        try {
            String otp = String.valueOf(new Random().nextInt(9000000) + 1000000);

            OtpToken otpToken = OtpToken.builder()
                    .identifier(identifier)
                    .otpCode(otp)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .used(false)
                    .build();

            otpTokenRepository.save(otpToken);

            return otp;
        } catch (Exception ex) {

            throw new HRMSException("Failed to generate OTP", "OTP_GENERATION_FAILED",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean validateOtp(String identifier, String otpCode) {
        try {

            Optional<OtpToken> otpTokeOptional = otpTokenRepository
                    .findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(identifier);

            if (otpTokeOptional.isEmpty()) {
                throw new HRMSException("OTP not found for identifier: " + identifier, "OTP_NOT_FOUND",
                        HttpStatus.NOT_FOUND);
            }

            OtpToken otpToken = otpTokeOptional.get();

            if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new HRMSException("OTP expired. Please request a new OTP", "OTP_EXPIRED", HttpStatus.BAD_REQUEST);
            }

            if (!otpToken.getOtpCode().equals(otpCode)) {
                throw new HRMSException("Invalid OTP entered", "OTP_INVALID", HttpStatus.BAD_REQUEST);
            }

            otpToken.setUsed(true);
            otpTokenRepository.save(otpToken);

            return true;
        } catch (HRMSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new HRMSException("Error validating  OTP", "OTP_VALIDATION_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
