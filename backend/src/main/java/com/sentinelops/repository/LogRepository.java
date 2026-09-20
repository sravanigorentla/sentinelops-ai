package com.sentinelops.repository;

import com.sentinelops.domain.entity.LogEntity;
import com.sentinelops.domain.enums.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
    
    @Query("SELECT l FROM LogEntity l WHERE " +
           "(:serviceId IS NULL OR l.service.id = :serviceId) AND " +
           "(:logLevel IS NULL OR l.logLevel = :logLevel) AND " +
           "(:search IS NULL OR LOWER(l.message) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.requestId) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LogEntity> searchLogs(@Param("serviceId") Long serviceId,
                               @Param("logLevel") LogLevel logLevel,
                               @Param("search") String search,
                               Pageable pageable);

    @Query("SELECT l FROM LogEntity l WHERE l.service.id = :serviceId AND l.timestamp >= :since ORDER BY l.timestamp DESC")
    List<LogEntity> findRecentLogsForService(@Param("serviceId") Long serviceId, @Param("since") ZonedDateTime since);
}
