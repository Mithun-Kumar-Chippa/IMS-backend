package com.example.internshipmanagement.service;

import com.example.internshipmanagement.entity.AuditLog;
import com.example.internshipmanagement.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void logAction(String action, String performedBy, String userEmail, String details, String ipAddress) {
        try {
            AuditLog log = new AuditLog(action, performedBy, userEmail, details, ipAddress);
            auditLogRepository.save(log);
            logger.info("AUDIT: action='{}', user='{}', email='{}', details='{}'", action, performedBy, userEmail, details);
        } catch (Exception ex) {
            logger.error("Failed to save audit log entry", ex);
        }
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
