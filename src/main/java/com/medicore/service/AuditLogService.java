package com.medicore.service;

import com.medicore.domain.entity.AuditLog;
import com.medicore.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void logAccess(Long userId, String action, String resourceType, String result) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .resourceType(resourceType)
                .result(result)
                .build();
        auditLogRepository.save(log);
    }
}
