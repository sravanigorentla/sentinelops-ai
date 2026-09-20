package com.sentinelops.service;

import com.sentinelops.domain.entity.*;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.domain.enums.LogLevel;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.AIAnalysisDto;
import com.sentinelops.dto.LogDto;
import com.sentinelops.dto.MetricDto;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.AIAnalysisRepository;
import com.sentinelops.repository.DeploymentRepository;
import com.sentinelops.repository.IncidentRepository;
import com.sentinelops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceClientTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private AIAnalysisRepository aiAnalysisRepository;
    @Mock private MetricLogService metricLogService;
    @Mock private DeploymentRepository deploymentRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private AIServiceClient aiServiceClient;

    private IncidentEntity incident;
    private ServiceEntity service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new ServiceEntity();
        service.setId(1L);
        service.setName("auth-service");
        service.setEnvironment("production");
        service.setStatus(ServiceStatus.DEGRADED);

        incident = new IncidentEntity();
        incident.setId(10L);
        incident.setTitle("Authentication Failure Rate Spike");
        incident.setDescription("504 Gateway Timeouts on /api/v1/auth/login");
        incident.setSeverity(IncidentSeverity.CRITICAL);
        incident.setStatus(IncidentStatus.INVESTIGATING);
        incident.setService(service);

        Role adminRole = new Role(1L, com.sentinelops.domain.enums.RoleName.ROLE_ADMIN);
        user = new User("admin@sentinelops.io", "password", "Admin User", adminRole);
        user.setId(1L);
    }

    @Test
    void testAnalyzeIncident_FallbackAnalyzer_Success() {
        when(incidentRepository.findById(10L)).thenReturn(Optional.of(incident));
        LogDto errorLog = new LogDto();
        errorLog.setId(100L);
        errorLog.setServiceId(1L);
        errorLog.setServiceName("auth-service");
        errorLog.setLogLevel(LogLevel.ERROR);
        errorLog.setMessage("Connection pool timeout waiting for database socket");
        errorLog.setTimestamp(ZonedDateTime.now());
        when(metricLogService.getRecentLogsForService(1L, 60)).thenReturn(List.of(errorLog));
        when(metricLogService.getMetricsForService(1L, 10)).thenReturn(Collections.emptyList());
        when(deploymentRepository.findByServiceIdOrderByDeploymentTimeDesc(1L)).thenReturn(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AIAnalysisEntity savedEntity = new AIAnalysisEntity();
        savedEntity.setId(100L);
        savedEntity.setIncident(incident);
        savedEntity.setSummary("Automated AI Analysis for auth-service incident #10.");
        savedEntity.setProbableRootCause("Connection pool exhaustion or memory heap pressure introduced during recent release.");
        savedEntity.setConfidence(0.89);
        savedEntity.setVerifiedBy(user);

        when(aiAnalysisRepository.findByIncidentId(10L)).thenReturn(Optional.empty());
        when(aiAnalysisRepository.save(any(AIAnalysisEntity.class))).thenReturn(savedEntity);

        AIAnalysisDto result = aiServiceClient.analyzeIncident(10L, 1L, "admin@sentinelops.io", "127.0.0.1");

        assertNotNull(result);
        assertEquals(0.89, result.getConfidence());
        assertTrue(result.getProbableRootCause().contains("Connection pool"));
        verify(auditLogService, times(1)).logAction(eq(1L), eq("admin@sentinelops.io"), eq("AI_ANALYSIS_TRIGGER"), eq("INCIDENT"), eq("10"), any(), eq("127.0.0.1"));
    }

    @Test
    void testAnalyzeIncident_NotFound_ThrowsException() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                aiServiceClient.analyzeIncident(999L, 1L, "admin@sentinelops.io", "127.0.0.1")
        );
    }
}
