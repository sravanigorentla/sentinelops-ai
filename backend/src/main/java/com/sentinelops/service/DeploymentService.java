package com.sentinelops.service;

import com.sentinelops.domain.entity.DeploymentEntity;
import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.DeploymentStatus;
import com.sentinelops.dto.CreateDeploymentRequest;
import com.sentinelops.dto.DeploymentDto;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.DeploymentRepository;
import com.sentinelops.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final ServiceRepository serviceRepository;
    private final AuditLogService auditLogService;

    public DeploymentService(DeploymentRepository deploymentRepository,
                             ServiceRepository serviceRepository,
                             AuditLogService auditLogService) {
        this.deploymentRepository = deploymentRepository;
        this.serviceRepository = serviceRepository;
        this.auditLogService = auditLogService;
    }

    public List<DeploymentDto> getAllDeployments() {
        return deploymentRepository.findAll().stream().map(DeploymentDto::new).collect(Collectors.toList());
    }

    public List<DeploymentDto> getDeploymentsForService(Long serviceId) {
        return deploymentRepository.findByServiceIdOrderByDeploymentTimeDesc(serviceId)
                .stream().map(DeploymentDto::new).collect(Collectors.toList());
    }

    public DeploymentDto createDeployment(CreateDeploymentRequest request, Long userId, String userEmail, String ipAddress) {
        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));

        DeploymentEntity deployment = new DeploymentEntity();
        deployment.setService(service);
        deployment.setVersion(request.getVersion());
        deployment.setCommitSha(request.getCommitSha());
        deployment.setBranch(request.getBranch());
        deployment.setEnvironment(request.getEnvironment());
        deployment.setDeployedBy(request.getDeployedBy() != null ? request.getDeployedBy() : userEmail);
        deployment.setDeploymentTime(ZonedDateTime.now());
        deployment.setStatus(DeploymentStatus.SUCCESS);

        service.setVersion(request.getVersion());
        serviceRepository.save(service);

        DeploymentEntity saved = deploymentRepository.save(deployment);
        auditLogService.logAction(userId, userEmail, "DEPLOYMENT_CREATE", "DEPLOYMENT", saved.getId().toString(),
                "Deployed " + saved.getVersion() + " to " + saved.getEnvironment(), ipAddress);

        return new DeploymentDto(saved);
    }
}
