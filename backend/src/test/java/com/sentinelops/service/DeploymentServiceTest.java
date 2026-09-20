package com.sentinelops.service;

import com.sentinelops.domain.entity.DeploymentEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.DeploymentStatus;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.CreateDeploymentRequest;
import com.sentinelops.dto.DeploymentDto;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.DeploymentRepository;
import com.sentinelops.repository.ServiceRepository;
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
class DeploymentServiceTest {

    @Mock private DeploymentRepository deploymentRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private DeploymentService deploymentService;

    private ServiceEntity service;
    private DeploymentEntity deployment;

    @BeforeEach
    void setUp() {
        service = new ServiceEntity();
        service.setId(1L);
        service.setName("order-service");
        service.setEnvironment("production");
        service.setVersion("v3.0.0");
        service.setStatus(ServiceStatus.HEALTHY);

        deployment = new DeploymentEntity();
        deployment.setId(10L);
        deployment.setService(service);
        deployment.setVersion("v3.1.0");
        deployment.setCommitSha("abc1234567890");
        deployment.setBranch("release/v3.1");
        deployment.setEnvironment("production");
        deployment.setDeployedBy("ci-pipeline@sentinelops.io");
        deployment.setDeploymentTime(ZonedDateTime.now());
        deployment.setStatus(DeploymentStatus.SUCCESS);
    }

    @Test
    void testGetAllDeployments_ReturnsList() {
        when(deploymentRepository.findAll()).thenReturn(List.of(deployment));

        List<DeploymentDto> result = deploymentService.getAllDeployments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("v3.1.0", result.get(0).getVersion());
    }

    @Test
    void testGetDeploymentsForService_ReturnsList() {
        when(deploymentRepository.findByServiceIdOrderByDeploymentTimeDesc(1L)).thenReturn(List.of(deployment));

        List<DeploymentDto> result = deploymentService.getDeploymentsForService(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("abc1234567890", result.get(0).getCommitSha());
    }

    @Test
    void testGetDeploymentsForService_Empty() {
        when(deploymentRepository.findByServiceIdOrderByDeploymentTimeDesc(999L)).thenReturn(Collections.emptyList());

        List<DeploymentDto> result = deploymentService.getDeploymentsForService(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateDeployment_Success() {
        CreateDeploymentRequest request = new CreateDeploymentRequest();
        request.setServiceId(1L);
        request.setVersion("v3.2.0");
        request.setCommitSha("def4567890abc");
        request.setBranch("main");
        request.setEnvironment("production");
        request.setDeployedBy("dev@sentinelops.io");

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(deploymentRepository.save(any(DeploymentEntity.class))).thenAnswer(invocation -> {
            DeploymentEntity entity = invocation.getArgument(0);
            entity.setId(20L);
            return entity;
        });
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        DeploymentDto result = deploymentService.createDeployment(request, 1L, "dev@sentinelops.io", "10.0.0.5");

        assertNotNull(result);
        assertEquals("v3.2.0", result.getVersion());
        assertEquals("v3.2.0", service.getVersion()); // Service version should be updated
        verify(auditLogService, times(1)).logAction(eq(1L), eq("dev@sentinelops.io"), eq("DEPLOYMENT_CREATE"), eq("DEPLOYMENT"), eq("20"), any(), eq("10.0.0.5"));
    }

    @Test
    void testCreateDeployment_ServiceNotFound_ThrowsException() {
        CreateDeploymentRequest request = new CreateDeploymentRequest();
        request.setServiceId(999L);

        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                deploymentService.createDeployment(request, 1L, "dev@sentinelops.io", "127.0.0.1"));
    }
}
