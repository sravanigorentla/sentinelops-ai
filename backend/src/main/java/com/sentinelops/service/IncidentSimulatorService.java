package com.sentinelops.service;

import com.sentinelops.domain.entity.MetricEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.LogLevel;
import com.sentinelops.dto.IncidentDto;
import com.sentinelops.dto.SimulateIncidentRequest;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IncidentSimulatorService {

    private final MetricLogService metricLogService;
    private final ServiceRepository serviceRepository;
    private final ServiceManagementService serviceManagementService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final IncidentService incidentService;
    private final AuditLogService auditLogService;

    public IncidentSimulatorService(MetricLogService metricLogService,
                                   ServiceRepository serviceRepository,
                                   ServiceManagementService serviceManagementService,
                                   AnomalyDetectionService anomalyDetectionService,
                                   IncidentService incidentService,
                                   AuditLogService auditLogService) {
        this.metricLogService = metricLogService;
        this.serviceRepository = serviceRepository;
        this.serviceManagementService = serviceManagementService;
        this.anomalyDetectionService = anomalyDetectionService;
        this.incidentService = incidentService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public IncidentDto simulateIncident(SimulateIncidentRequest request, Long userId, String userEmail, String ipAddress) {
        Long serviceId = request.getServiceId();
        ServiceEntity serviceEntity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        String type = request.getSimulationType() != null ? request.getSimulationType().toUpperCase() : "DB_FAILURE";
        String reqId = "req-sim-" + UUID.randomUUID().toString().substring(0, 8);

        switch (type) {
            case "MEMORY_LEAK" -> {
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "Fatal: java.lang.OutOfMemoryError: Java heap space at com.sentinelops.service.DataProcessor.allocateCache", reqId, "{\"heap_max\":\"4096M\"}");
                metricLogService.ingestLog(serviceId, "production", LogLevel.WARN,
                        "GC overhead limit exceeded: 98.4% time spent in Garbage Collection", reqId, "{\"gc_time_ms\":4800}");
                metricLogService.ingestMetric(serviceId, 94.5, 98.8, 180.0, 1850.0, 15.4);
            }
            case "HIGH_LATENCY" -> {
                metricLogService.ingestLog(serviceId, "production", LogLevel.WARN,
                        "API Gateway upstream timeout waiting for service response after 5000ms", reqId, "{\"gateway_timeout\":5000}");
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "Thread pool saturation: 200/200 active worker threads executing requests", reqId, "{\"active_threads\":200}");
                metricLogService.ingestMetric(serviceId, 92.1, 75.0, 310.0, 4200.0, 8.5);
            }
            case "HIGH_ERROR_RATE" -> {
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "HTTP 500 Internal Server Error: Failed to deserialize JSON payload from payment gateway", reqId, "{\"status_code\":500}");
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "Unhandled NullPointerException in TransactionController line 84", reqId, "{\"exception\":\"NullPointerException\"}");
                metricLogService.ingestMetric(serviceId, 55.0, 60.0, 450.0, 350.0, 85.0);
            }
            default -> { // DB_FAILURE
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "PSQLException: FATAL: remaining connection slots are reserved for non-replication superuser connections", reqId, "{\"db_connections\":100}");
                metricLogService.ingestLog(serviceId, "production", LogLevel.ERROR,
                        "HikariPool-1 - Connection is not available, request timed out after 30000ms", reqId, "{\"pool\":\"PrimaryDS\"}");
                metricLogService.ingestMetric(serviceId, 88.0, 82.0, 120.0, 3800.0, 42.0);
            }
        }

        // Trigger Anomaly Evaluation with fully populated ServiceEntity
        var latestMetrics = metricLogService.getMetricsForService(serviceId, 1);
        if (!latestMetrics.isEmpty()) {
            var metric = new MetricEntity();
            metric.setService(serviceEntity);
            metric.setCpuUsage(latestMetrics.get(0).getCpuUsage());
            metric.setMemoryUsage(latestMetrics.get(0).getMemoryUsage());
            metric.setRequestRate(latestMetrics.get(0).getRequestRate());
            metric.setLatencyMs(latestMetrics.get(0).getLatencyMs());
            metric.setErrorRate(latestMetrics.get(0).getErrorRate());
            
            anomalyDetectionService.evaluateMetric(metric);
        }

        auditLogService.logAction(userId, userEmail, "SIMULATE_INCIDENT", "SERVICE", serviceId.toString(),
                "Triggered simulated " + type + " failure scenario", ipAddress);

        // Fetch created active incident
        var incidents = incidentService.filterIncidents(serviceId, null, null, 0, 1);
        if (!incidents.getContent().isEmpty()) {
            return incidents.getContent().get(0);
        }

        // Fallback return if no new incident created (e.g. status was already updated)
        var allIncidents = incidentService.filterIncidents(null, null, null, 0, 1);
        if (!allIncidents.getContent().isEmpty()) {
            return allIncidents.getContent().get(0);
        }

        throw new ResourceNotFoundException("No incident found for service " + serviceId);
    }
}
