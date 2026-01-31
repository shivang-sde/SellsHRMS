package com.sellspark.SellsHRMS.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.sellspark.SellsHRMS.exception.core.DbExceptionTranslator;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Aspect
@Component
@Slf4j
public class DbExceptionAspect {

    @Around("@annotation(com.sellspark.SellsHRMS.aop.TranslateDbException)")
    public Object translateDbException(ProceedingJoinPoint pjp) throws Throwable {
        String traceId = MDC.get("traceId");
        log.debug("[TRACE:{}] Entering {}.{}",
                traceId,
                pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName());
        try {
            Object result = pjp.proceed();
            log.debug("[TRACE:{}] Exiting {}.{} successfully",
                    traceId,
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName());
            return result;
        } catch (Throwable t) {
            // Log the actual runtime exception class for debugging
            log.error("[TRACE:{}] Exception in {}.{} -> {}: {}",
                    traceId,
                    pjp.getSignature().getDeclaringTypeName(),
                    pjp.getSignature().getName(),
                    t.getClass().getName(),
                    t.getMessage());

            // Let translator examine the throwable and return an HRMSException if
            // applicable.
            // If translator can't translate, rethrow original to be handled by global
            // handler.
            RuntimeException translated = DbExceptionTranslator.translate(t);
            if (translated != null) {
                throw translated;
            }
            throw t;
        }
    }
}
