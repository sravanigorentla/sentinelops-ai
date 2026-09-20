package com.sentinelops.repository;

import com.sentinelops.domain.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByIncidentIdOrderBySentAtDesc(Long incidentId);
    List<NotificationEntity> findTop50ByOrderBySentAtDesc();
}
