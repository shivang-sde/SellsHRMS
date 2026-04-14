package com.sellspark.SellsHRMS.service.verification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Client for the Sandbox API (https://developer.sandbox.co.in).
 *
 * Handles:
 * - JWT authentication with in-memory caching (23-hour TTL)
 * - PAN verification
 * - Aadhaar offline KYC (OTP generation + verification)
 * - GSTIN search
 */
@Slf4j
@Service
public class SandboxApiService {

    @Value("${sandbox.api.key:}")
    private String apiKey;

    @Value("${sandbox.api.secret:}")
    private String apiSecret;

    @Value("${sandbox.api.url:https://api.sandbox.co.in}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── In-memory token cache (instead of Redis) ───────────────────
    private String cachedToken;
    private Instant tokenExpiry;
    private final Object tokenLock = new Object();

    // ─── Public API Methods ─────────────────────────────────────────

    /**
     * Authenticates with Sandbox API and returns a JWT token.
     * Cached for 23 hours to avoid unnecessary re-authentication.
     */
    public String getAccessToken() {
        synchronized (tokenLock) {
            if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)) {
                log.debug("[SANDBOX] Using cached token (expires in {} minutes)",
                        ChronoUnit.MINUTES.between(Instant.now(), tokenExpiry));
                return cachedToken;
            }

            log.info("[SANDBOX] Authenticating with Sandbox API...");
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("x-api-key", apiKey);
                headers.set("x-api-secret", apiSecret);
                headers.set("x-api-version", "2.0");

                HttpEntity<String> entity = new HttpEntity<>("{}", headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        baseUrl + "/authenticate",
                        HttpMethod.POST,
                        entity,
                        String.class);

                JsonNode root = objectMapper.readTree(response.getBody());
                String token = root.path("access_token").asText();

                if (token == null || token.isBlank()) {
                    log.error("[SANDBOX] Authentication failed: {}", response.getBody());
                    throw new RuntimeException("Sandbox authentication failed");
                }

                cachedToken = token;
                tokenExpiry = Instant.now().plus(23, ChronoUnit.HOURS);
                log.info("[SANDBOX] Authentication successful. Token cached for 23 hours.");
                return cachedToken;

            } catch (Exception e) {
                log.error("[SANDBOX] Authentication error: {}", e.getMessage());
                throw new RuntimeException("Failed to authenticate with Sandbox API", e);
            }
        }
    }

    /**
     * Verifies PAN number via Sandbox API.
     * POST /kyc/pan/verify
     */
    public JsonNode verifyPan(String pan, String name, String dateOfBirth) {
        String token = getAccessToken();

        HttpHeaders headers = buildHeaders(token);
        Map<String, Object> body = new ConcurrentHashMap<>();
        body.put("@entity", "in.co.sandbox.kyc.pan_verification.request");
        body.put("pan", pan);
        if (name != null && !name.isBlank())
            body.put("name_as_per_pan", name);
        if (dateOfBirth != null && !dateOfBirth.isBlank())
            body.put("date_of_birth", dateOfBirth);
        body.put("consent", "y");
        body.put("reason", "Organisation document verification");

        return postRequest("/kyc/pan/verify", headers, body);
    }

    /**
     * Generates OTP for Aadhaar offline KYC.
     * POST /kyc/aadhaar/okyc/otp
     */
    public JsonNode generateAadhaarOtp(String aadhaarNumber) {
        if (!aadhaarNumber.matches("\\d{12}")) {
            throw new IllegalArgumentException("Invalid Aadhaar number");
        }
        String token = getAccessToken();

        HttpHeaders headers = buildHeaders(token);
        Map<String, Object> body = new ConcurrentHashMap<>();

        body.put("@entity", "in.co.sandbox.kyc.aadhaar.okyc.otp.request");
        body.put("aadhaar_number", aadhaarNumber);
        body.put("consent", "y");
        body.put("reason", "Organisation document verification");

        return postRequest("/kyc/aadhaar/okyc/otp", headers, body);
    }

    /**
     * Verifies OTP for Aadhaar offline KYC.
     * POST /kyc/aadhaar/okyc/otp/verify
     * Returns Aadhaar details including photo in base64.
     */
    public JsonNode verifyAadhaarOtp(String otp, String refId) {
        String token = getAccessToken();

        HttpHeaders headers = buildHeaders(token);
        Map<String, Object> body = new ConcurrentHashMap<>();
        body.put("@entity", "in.co.sandbox.kyc.aadhaar.okyc.request");
        body.put("otp", otp);
        body.put("reference_id", refId);

        return postRequest("/kyc/aadhaar/okyc/otp/verify", headers, body);
    }

    /**
     * Searches GSTIN details.
     * POST /gst/compliance/public/gstin/search
     */
    public JsonNode searchGstin(String gstin) {
        String token = getAccessToken();

        HttpHeaders headers = buildHeaders(token);
        Map<String, Object> body = new ConcurrentHashMap<>();
        body.put("gstin", gstin);

        return postRequest("/gst/compliance/public/gstin/search", headers, body);
    }

    // ─── Internal Helpers ────────────────────────────────────────────

    private HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token); // WITHOUT Bearer prefix per Sandbox spec
        headers.set("x-api-key", apiKey);
        headers.set("x-api-version", "2.0");
        return headers;
    }

    private JsonNode postRequest(String path, HttpHeaders headers, Map<String, Object> body) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            log.info("[SANDBOX] POST {} | Body: {}", path, jsonBody);

            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + path,
                    HttpMethod.POST,
                    entity,
                    String.class);

            log.info("[SANDBOX] Response {} | Status: {}", path, response.getStatusCode());

            return objectMapper.readTree(response.getBody());

        } catch (Exception e) {
            log.error("[SANDBOX] API call failed: {} | Error: {}", path, e.getMessage());
            throw new RuntimeException("Sandbox API call failed: " + path, e);
        }
    }

    /**
     * Scheduled cleanup: invalidate token if expired (failsafe).
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupToken() {
        synchronized (tokenLock) {
            if (tokenExpiry != null && Instant.now().isAfter(tokenExpiry)) {
                log.info("[SANDBOX] Cached token expired, clearing.");
                cachedToken = null;
                tokenExpiry = null;
            }
        }
    }
}
