package com.sellspark.SellsHRMS.service.auth;

public interface OtpService {

    /**
     * Generate a new OTP for a given identifier (email or phone)
     * 
     * @param identifier the email or phone number
     * @return generated OTP code (for simulation only)
     */
    String generateOtp(String identifier);

    /**
     * Validate an OTP for a given identifier.
     * Marks it as used if valid.
     * 
     * @param identifier email or phone
     * @param otpCode    OTP code to validate
     * @return true if valid
     */
    boolean validateOtp(String identifier, String otpCode);
}
