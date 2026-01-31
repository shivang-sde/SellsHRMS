package com.sellspark.SellsHRMS.utils;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC; // ✅ use SLF4J’s MDC instead of JBoss
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Automatically attaches a traceId to every HTTP request.
 * This traceId is stored in MDC so it appears in all logs for that request.
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Generate unique trace ID for each request
        String traceId = UUID.randomUUID().toString();

        // ✅ Put it into SLF4J's MDC so all logs include it automatically
        MDC.put("traceId", traceId);

        // Optionally include it in the response header (for client debugging)
        response.setHeader("X-Trace-Id", traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); // Prevent thread reuse leaks
        }
    }
}
