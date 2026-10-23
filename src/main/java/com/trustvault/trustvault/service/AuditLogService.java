
package com.trustvault.trustvault.service;

import com.trustvault.trustvault.entity.AuditLog;
import com.trustvault.trustvault.repository.AuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void recordEvent(
            String username,
            String action,
            String result) {

        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setResult(result);
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}

