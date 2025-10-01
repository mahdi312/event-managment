package com.event.management.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceLoggingAspect {

    @Value("${application.performance.threshold-ms:200}")
    private long thresholdMs;

    @Pointcut("within(com.event.management.userservice.service..*)")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            if (duration >= thresholdMs) {
                log.warn("Performance: {}.{} took {} ms (>= {} ms)",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        duration,
                        thresholdMs);
            } else if (log.isDebugEnabled()) {
                log.debug("Performance: {}.{} took {} ms",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        duration);
            }
        }
    }
}


