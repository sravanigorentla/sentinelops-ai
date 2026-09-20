package com.sentinelops.service;

import com.sentinelops.domain.entity.DeploymentEntity;
import com.sentinelops.domain.entity.IncidentEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.DeploymentStatus;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.domain.enums.RollbackStatus;
import com.sentinelops.dto.IncidentDto;
import com.sentinelops.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock private IncidentRepository incidentRepository;
    @Mock private IncidentCommentRepository commentRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private UserRepository userRepository;
    @Mock private DeploymentRepository deploymentRepository;
    @Mock private AIAnalysisRepository aiAnalysisRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private IncidentService incidentService;

    private IncidentEntity incident;
    private DeploymentEntity deployment;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        service = new ServiceEntity();
        service.setId(1L);
        service.setName("payment-service");

        deployment = new DeploymentEntity();
        deployment.setId(100L);
        deployment.setService(service);
        deployment.setVersion("v2.4.1");
        deployment.setCommitSha("a1b2c3d4e5f6");
        deployment.setBranch("main");
        deployment.setEnvironment("production");
        deployment.setDeployedBy("Sarah Connor");
        deployment.setDeploymentTime(ZonedDateTime.now().minusHours(1));
        deployment.setStatus(DeploymentStatus.SUCCESS);

        incident = new IncidentEntity();
        incident.setId(5L);
        incident.setTitle("Database Latency Spike");
        incident.setSeverity(IncidentSeverity.HIGH);
        incident.setStatus(IncidentStatus.INVESTIGATING);
        incident.setService(service);
        incident.setDeployment(deployment);
        incident.setDetectedAt(ZonedDateTime.now().minusMinutes(30));
        incident.setRollbackStatus(RollbackStatus.PENDING);
    }

    @Test
    void testTriggerDeploymentRollback_Success() {
        when(incidentRepository.findById(5L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(IncidentEntity.class))).thenAnswer(i -> i.getArgument(0));

        IncidentDto result = incidentService.triggerDeploymentRollback(5L, 2L, "dev@sentinelops.io", "127.0.0.1");

        assertNotNull(result);
        assertEquals(RollbackStatus.COMPLETED, result.getRollbackStatus());
        assertEquals(IncidentStatus.RESOLVED, result.getStatus());
        assertTrue(deployment.getRolledBack());
        assertNotNull(deployment.getRolledBackAt());

        verify(deploymentRepository, times(1)).save(deployment);
        verify(incidentRepository, times(1)).save(incident);
        verify(auditLogService, times(1)).logAction(eq(2L), eq("dev@sentinelops.io"), eq("DEPLOYMENT_ROLLBACK"), any(), any(), any(), any());
    }
}
