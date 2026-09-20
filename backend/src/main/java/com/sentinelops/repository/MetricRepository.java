package com.sentinelops.repository;

import com.sentinelops.domain.entity.MetricEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface MetricRepository extends JpaRepository<MetricEntity, Long> {
    List<MetricEntity> findByServiceIdOrderByTimestampDesc(Long serviceId, Pageable pageable);
    
    @Query("SELECT m FROM MetricEntity m WHERE m.service.id = :serviceId AND m.timestamp >= :since ORDER BY m.timestamp ASC")
    List<MetricEntity> findRecentMetricsForService(@Param("serviceId") Long serviceId, @Param("since") ZonedDateTime since);
}
