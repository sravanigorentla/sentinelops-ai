package com.sentinelops.repository;

import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByEnvironment(String environment);
    List<ServiceEntity> findByStatus(ServiceStatus status);
}
