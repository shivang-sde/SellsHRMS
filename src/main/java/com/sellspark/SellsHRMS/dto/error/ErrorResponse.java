package com.sellspark.SellsHRMS.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard error response structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private int status;
    
    private String error;
    
    private String errorCode;
    
    private String message;
    
    private String path;

    private String traceId; // For correlating logs with client-side errors
    
    private List<ValidationError> validationErrors;
    
    private Map<String, Object> additionalInfo;

    /**
     * Validation error details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

    /**
     * Create error response for general exceptions
     */
    public static ErrorResponse of(int status, String error, String errorCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .traceId(java.util.UUID.randomUUID().toString())
                .build();
    }

    /**
     * Create error response with validation errors
     */
    public static ErrorResponse withValidationErrors(
            int status, 
            String message, 
            String path, 
            List<ValidationError> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error("Validation Failed")
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .path(path)
                 .traceId(java.util.UUID.randomUUID().toString())
                .validationErrors(validationErrors)
                .build();
    }
}