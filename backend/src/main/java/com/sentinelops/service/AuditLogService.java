package com.sentinelops.service;

import com.sentinelops.domain.entity.AuditLogEntity;
import com.sentinelops.dto.AuditLogDto;
import com.sentinelops.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(Long userId, String userEmail, String action, String targetType, String targetId, String details, String ipAddress) {
        AuditLogEntity log = new AuditLogEntity();
        log.setUserId(userId);
        log.setUserEmail(userEmail);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setTimestamp(ZonedDateTime.now());
        auditLogRepository.save(log);
    }

    public Page<AuditLogDto> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable).map(AuditLogDto::new);
    }

    public List<AuditLogDto> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc()
                .stream().map(AuditLogDto::new).collect(Collectors.toList());
    }
}
