package com.sentinelops.service;

import com.sentinelops.domain.entity.MetricEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.DashboardSummaryDto;
import com.sentinelops.repository.DeploymentRepository;
import com.sentinelops.repository.IncidentRepository;
import com.sentinelops.repository.MetricRepository;
import com.sentinelops.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private ServiceRepository serviceRepository;
    @Mock private IncidentRepository incidentRepository;
    @Mock private DeploymentRepository deploymentRepository;
    @Mock private MetricRepository metricRepository;
    @Mock private ServiceManagementService serviceManagementService;
    @Mock private IncidentService incidentService;
    @Mock private DeploymentService deploymentService;

    @InjectMocks private DashboardService dashboardService;

    private ServiceEntity healthyService;
    private ServiceEntity degradedService;

    @BeforeEach
    void setUp() {
        healthyService = new ServiceEntity();
        healthyService.setId(1L);
        healthyService.setName("order-service");
        healthyService.setStatus(ServiceStatus.HEALTHY);

        degradedService = new ServiceEntity();
        degradedService.setId(2L);
        degradedService.setName("payment-service");
        degradedService.setStatus(ServiceStatus.DEGRADED);
    }

    @Test
    void testGetDashboardSummary_CalculatesMetricsCorrectly() {
        when(serviceRepository.findAll()).thenReturn(List.of(healthyService, degradedService));
        when(incidentRepository.countByStatusNot(IncidentStatus.RESOLVED)).thenReturn(2L);
        when(incidentRepository.countBySeverityAndStatusNot(IncidentSeverity.CRITICAL, IncidentStatus.RESOLVED)).thenReturn(1L);

        MetricEntity metric = new MetricEntity();
        metric.setCpuUsage(45.0);
        metric.setMemoryUsage(60.0);
        metric.setRequestRate(1000.0);
        metric.setLatencyMs(150.0);
        metric.setErrorRate(0.5);

        when(metricRepository.findAll(PageRequest.of(0, 50))).thenReturn(new PageImpl<>(List.of(metric)));
        when(serviceManagementService.getAllServices()).thenReturn(Collections.emptyList());
        when(incidentService.filterIncidents(null, null, null, 0, 5)).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(deploymentService.getAllDeployments()).thenReturn(Collections.emptyList());

        DashboardSummaryDto result = dashboardService.getDashboardSummary();

        assertNotNull(result);
        assertEquals(2, result.getTotalServices());
        assertEquals(1, result.getHealthyServices());
        assertEquals(1, result.getDegradedServices());
        assertEquals(0, result.getDownServices());
        assertEquals(2L, result.getActiveIncidents());
        assertEquals(1L, result.getCriticalIncidents());
        assertEquals(45.0, result.getAvgCpuUsage());
        assertEquals(60.0, result.getAvgMemoryUsage());
        assertEquals(1000.0, result.getTotalRequestRate());
        assertEquals(150.0, result.getAvgLatencyMs());
        assertEquals(0.5, result.getAvgErrorRate());
    }
}
