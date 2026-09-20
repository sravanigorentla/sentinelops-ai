package com.sentinelops.repository;

import com.sentinelops.domain.entity.AIAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIAnalysisRepository extends JpaRepository<AIAnalysisEntity, Long> {
    Optional<AIAnalysisEntity> findByIncidentId(Long incidentId);
}
