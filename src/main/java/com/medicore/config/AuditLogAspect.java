package com.medicore.config;

import com.medicore.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(audited)", returning = "result")
    public void logAudit(JoinPoint joinPoint, Audited audited, Object result) {
        Long userId = 1L; // Mock user ID
        auditLogService.logAccess(userId, audited.action(), audited.resource(), "SUCCESS");
    }
}
