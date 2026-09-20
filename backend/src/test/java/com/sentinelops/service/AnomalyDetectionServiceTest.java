package com.sentinelops.service;

import com.sentinelops.domain.entity.MetricEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.repository.MetricRepository;
import com.sentinelops.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnomalyDetectionServiceTest {

    @Mock
    private MetricRepository metricRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private IncidentService incidentService;

    @Mock
    private AlertNotificationService notificationService;

    @InjectMocks
    private AnomalyDetectionService anomalyDetectionService;

    private ServiceEntity testService;

    @BeforeEach
    void setUp() {
        testService = new ServiceEntity();
        testService.setId(1L);
        testService.setName("order-service");
        testService.setStatus(ServiceStatus.HEALTHY);
    }

    @Test
    void evaluateMetric_NoHistory_ShouldDoNothing() {
        MetricEntity metric = createMetric(10.0, 50.0, 2.0, 50.0);
        when(metricRepository.findByServiceIdOrderByTimestampDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of());

        anomalyDetectionService.evaluateMetric(metric);

        verify(incidentService, never()).triggerAutomaticIncident(any(), any(), any(), any());
        verify(notificationService, never()).sendAlertNotifications(any(), any(), any(), any());
    }

    @Test
    void evaluateMetric_CriticalThresholdExceeded_TriggersIncident() {
        MetricEntity metric = createMetric(55.0, 3500.0, 80.0, 80.0);
        when(metricRepository.findByServiceIdOrderByTimestampDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(metric));

        anomalyDetectionService.evaluateMetric(metric);

        verify(serviceRepository).save(argThat(s -> s.getStatus() == ServiceStatus.DOWN));
        verify(incidentService).triggerAutomaticIncident(eq(testService), contains("Critical Telemetry Anomaly"), anyString(), eq(IncidentSeverity.CRITICAL));
        verify(notificationService).sendAlertNotifications(eq(testService), contains("Critical Telemetry Anomaly"), anyString(), eq(IncidentSeverity.CRITICAL));
    }

    @Test
    void evaluateMetric_HighThresholdExceeded_TriggersIncident() {
        MetricEntity metric = createMetric(10.0, 1500.0, 92.0, 50.0);
        when(metricRepository.findByServiceIdOrderByTimestampDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(metric));

        anomalyDetectionService.evaluateMetric(metric);

        verify(serviceRepository).save(argThat(s -> s.getStatus() == ServiceStatus.DEGRADED));
        verify(incidentService).triggerAutomaticIncident(eq(testService), contains("High Severity Telemetry Anomaly"), anyString(), eq(IncidentSeverity.HIGH));
        verify(notificationService).sendAlertNotifications(eq(testService), contains("High Severity Telemetry Anomaly"), anyString(), eq(IncidentSeverity.HIGH));
    }

    @Test
    void evaluateMetric_NormalMetrics_NoAnomaly() {
        MetricEntity metric = createMetric(1.0, 100.0, 40.0, 50.0);
        List<MetricEntity> history = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            history.add(createMetric(1.0, 100.0 + (i % 3), 40.0, 50.0));
        }
        when(metricRepository.findByServiceIdOrderByTimestampDesc(eq(1L), any(Pageable.class)))
                .thenReturn(history);

        anomalyDetectionService.evaluateMetric(metric);

        verify(incidentService, never()).triggerAutomaticIncident(any(), any(), any(), any());
    }

    @Test
    void evaluateMetric_StatisticalZScoreAnomaly_TriggersIncident() {
        MetricEntity metric = createMetric(1.0, 500.0, 40.0, 50.0); // Spikes to 500ms
        List<MetricEntity> history = List.of(
                createMetric(1.0, 100.0, 40.0, 50.0),
                createMetric(1.0, 102.0, 40.0, 50.0),
                createMetric(1.0, 98.0, 40.0, 50.0),
                createMetric(1.0, 101.0, 40.0, 50.0),
                createMetric(1.0, 99.0, 40.0, 50.0)
        );
        when(metricRepository.findByServiceIdOrderByTimestampDesc(eq(1L), any(Pageable.class)))
                .thenReturn(history);

        anomalyDetectionService.evaluateMetric(metric);

        verify(serviceRepository).save(argThat(s -> s.getStatus() == ServiceStatus.DEGRADED));
        verify(incidentService).triggerAutomaticIncident(eq(testService), contains("Statistical Latency Anomaly"), anyString(), eq(IncidentSeverity.HIGH));
    }

    private MetricEntity createMetric(double errorRate, double latencyMs, double cpuUsage, double memoryUsage) {
        MetricEntity m = new MetricEntity();
        m.setService(testService);
        m.setErrorRate(errorRate);
        m.setLatencyMs(latencyMs);
        m.setCpuUsage(cpuUsage);
        m.setMemoryUsage(memoryUsage);
        m.setTimestamp(ZonedDateTime.now());
        return m;
    }
}
