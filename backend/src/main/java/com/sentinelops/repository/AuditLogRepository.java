package com.sentinelops.repository;

import com.sentinelops.domain.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    Page<AuditLogEntity> findAllByOrderByTimestampDesc(Pageable pageable);
    List<AuditLogEntity> findTop100ByOrderByTimestampDesc();
}
