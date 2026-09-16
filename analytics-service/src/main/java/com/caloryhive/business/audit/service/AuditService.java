package com.caloryhive.business.audit.service;

import com.caloryhive.business.audit.entity.AuditLog;
import com.caloryhive.business.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID businessId, UUID userId, String action, String entityType, String entityId, String metadata, String ipAddress, String userAgent) {
        try {
            AuditLog entry = AuditLog.builder()
                    .businessId(businessId)
                    .userId(userId)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .metadata(metadata)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            auditLogRepository.save(entry);
            log.info("Audit log recorded: action={}, entityType={}, entityId={}, businessId={}", action, entityType, entityId, businessId);
        } catch (Exception e) {
            log.error("Failed to persist audit log: {}", e.getMessage(), e);
        }
    }
}
