package com.sentinelops.service;

import com.sentinelops.domain.entity.MetricEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.repository.MetricRepository;
import com.sentinelops.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnomalyDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionService.class);

    private final MetricRepository metricRepository;
    private final ServiceRepository serviceRepository;
    private final IncidentService incidentService;
    private final AlertNotificationService notificationService;

    public AnomalyDetectionService(MetricRepository metricRepository,
                                   ServiceRepository serviceRepository,
                                   IncidentService incidentService,
                                   AlertNotificationService notificationService) {
        this.metricRepository = metricRepository;
        this.serviceRepository = serviceRepository;
        this.incidentService = incidentService;
        this.notificationService = notificationService;
    }

    public void evaluateMetric(MetricEntity metric) {
        ServiceEntity service = metric.getService();
        Long serviceId = service.getId();

        // Fetch last 20 metrics for baseline statistics
        Pageable pageable = PageRequest.of(0, 20, Sort.by("timestamp").descending());
        List<MetricEntity> history = metricRepository.findByServiceIdOrderByTimestampDesc(serviceId, pageable);

        if (history.isEmpty()) {
            return;
        }

        boolean anomalyDetected = false;
        String anomalyTitle = "";
        String anomalyDetails = "";
        IncidentSeverity severity = IncidentSeverity.MEDIUM;

        // 1. Threshold checks
        if (metric.getErrorRate() >= 50.0 || metric.getLatencyMs() >= 3000.0) {
            anomalyDetected = true;
            severity = IncidentSeverity.CRITICAL;
            anomalyTitle = "Critical Telemetry Anomaly on " + service.getName();
            anomalyDetails = String.format("Error rate at %.2f%% and latency at %.2fms exceed critical thresholds.", metric.getErrorRate(), metric.getLatencyMs());
        } else if (metric.getErrorRate() >= 5.0 || metric.getLatencyMs() >= 1000.0 || metric.getCpuUsage() >= 90.0 || metric.getMemoryUsage() >= 90.0) {
            anomalyDetected = true;
            severity = IncidentSeverity.HIGH;
            anomalyTitle = "High Severity Telemetry Anomaly on " + service.getName();
            anomalyDetails = String.format("High resource or error spike: CPU %.1f%%, Memory %.1f%%, Latency %.1fms, Errors %.2f%%.",
                    metric.getCpuUsage(), metric.getMemoryUsage(), metric.getLatencyMs(), metric.getErrorRate());
        }

        // 2. Statistical Z-Score check on Latency & Error Rate if history >= 5
        if (!anomalyDetected && history.size() >= 5) {
            double avgLatency = history.stream().mapToDouble(MetricEntity::getLatencyMs).average().orElse(0.0);
            double stdLatency = Math.sqrt(history.stream().mapToDouble(m -> Math.pow(m.getLatencyMs() - avgLatency, 2)).sum() / history.size());

            if (stdLatency > 0) {
                double zScore = (metric.getLatencyMs() - avgLatency) / stdLatency;
                if (zScore >= 3.0) {
                    anomalyDetected = true;
                    severity = IncidentSeverity.HIGH;
                    anomalyTitle = "Statistical Latency Anomaly on " + service.getName();
                    anomalyDetails = String.format("Z-Score %.2f detected for latency (Current: %.1fms vs Avg: %.1fms).", zScore, metric.getLatencyMs(), avgLatency);
                }
            }
        }

        if (anomalyDetected) {
            logger.warn("Anomaly detected for service {}: {}", service.getName(), anomalyTitle);
            
            // Update Service Status
            ServiceStatus newStatus = severity == IncidentSeverity.CRITICAL ? ServiceStatus.DOWN : ServiceStatus.DEGRADED;
            service.setStatus(newStatus);
            serviceRepository.save(service);

            // Trigger Incident Creation automatically
            incidentService.triggerAutomaticIncident(service, anomalyTitle, anomalyDetails, severity);

            // Trigger Notifications
            notificationService.sendAlertNotifications(service, anomalyTitle, anomalyDetails, severity);
        }
    }
}
