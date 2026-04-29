package com.sellspark.SellsHRMS.notification.logger;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StructuredLogger {

    /*
     * Logging is done in JSON format
     */
    public void logEmailSent(String orgId, String eventCode, String recipient, String senderType) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", LocalDateTime.now());
        fields.put("event", "EMAIL_SENT");
        fields.put("eventCode", eventCode);
        fields.put("orgId", orgId);
        fields.put("recipient", recipient);
        fields.put("senderType", senderType);
        fields.put("message", "Email sent successfully");

        log.info("{}", fields);
    }

    /**
     * Log failed email with error details
     */
    public void logEmailFailed(String orgId, String eventCode, String recipient,
            String error, String senderType) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", LocalDateTime.now());
        fields.put("event", "EMAIL_FAILED");
        fields.put("orgId", orgId.toString());
        fields.put("eventCode", eventCode);
        fields.put("recipient", maskEmail(recipient));
        fields.put("senderType", senderType);
        fields.put("error", sanitizeError(error));

        log.error("{}", formatStructuredLog(fields));
    }

    /**
     * Log successful SMTP test
     */
    public void logSmtpTestSuccess(String orgId, String smtpHost) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", LocalDateTime.now());
        fields.put("event", "SMTP_TEST_SUCCESS");
        fields.put("orgId", orgId);
        fields.put("smtpHost", smtpHost);

        log.info("{}", formatStructuredLog(fields));
    }

    /**
     * Log rate limit exceeded
     */
    public void logRateLimitExceeded(String orgId, String eventId) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", LocalDateTime.now());
        fields.put("event", "RATE_LIMIT_EXCEEDED");
        fields.put("orgId", orgId);
        fields.put("eventId", eventId);

        log.warn("{}", formatStructuredLog(fields));
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;
        String[] parts = email.split("@");
        if (parts[0].length() <= 2)
            return email;
        return parts[0].charAt(0) + "***@" + parts[1];
    }

    private String sanitizeError(String error) {
        if (error == null)
            return null;
        // Remove sensitive info from error messages
        return error.replaceAll("(password|secret|key)[\"']?\\s*[:=]\\s*[^,\\s]+", "$1=***");
    }

    private String formatStructuredLog(Map<String, Object> fields) {
        StringBuilder sb = new StringBuilder("[NOTIFICATION]");
        fields.forEach((k, v) -> sb.append(" ").append(k).append("=").append(v));
        return sb.toString();
    }
}
