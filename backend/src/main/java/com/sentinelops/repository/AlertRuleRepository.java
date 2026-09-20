package com.sentinelops.repository;

import com.sentinelops.domain.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByServiceIdAndEnabledTrue(Long serviceId);
    List<AlertRule> findByEnabledTrue();
}
