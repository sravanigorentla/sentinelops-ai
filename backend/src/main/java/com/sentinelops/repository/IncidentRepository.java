package com.sentinelops.repository;

import com.sentinelops.domain.entity.IncidentEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {
    
    @Query("SELECT i FROM IncidentEntity i WHERE " +
           "(:serviceId IS NULL OR i.service.id = :serviceId) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:severity IS NULL OR i.severity = :severity)")
    Page<IncidentEntity> filterIncidents(@Param("serviceId") Long serviceId,
                                        @Param("status") IncidentStatus status,
                                        @Param("severity") IncidentSeverity severity,
                                        Pageable pageable);

    List<IncidentEntity> findByStatus(IncidentStatus status);
    List<IncidentEntity> findByServiceIdAndStatusNot(Long serviceId, IncidentStatus status);
    Long countByStatusNot(IncidentStatus status);
    Long countBySeverityAndStatusNot(IncidentSeverity severity, IncidentStatus status);
}
