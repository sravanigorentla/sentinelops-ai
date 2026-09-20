package com.sentinelops.repository;

import com.sentinelops.domain.entity.DeploymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<DeploymentEntity, Long> {
    List<DeploymentEntity> findByServiceIdOrderByDeploymentTimeDesc(Long serviceId);
    List<DeploymentEntity> findByServiceIdAndDeploymentTimeAfterOrderByDeploymentTimeDesc(Long serviceId, ZonedDateTime since);
}
