package com.sentinelops.service;

import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.CreateServiceRequest;
import com.sentinelops.dto.ServiceDto;
import com.sentinelops.exception.BadRequestException;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final AuditLogService auditLogService;

    public ServiceManagementService(ServiceRepository serviceRepository, AuditLogService auditLogService) {
        this.serviceRepository = serviceRepository;
        this.auditLogService = auditLogService;
    }

    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream().map(ServiceDto::new).collect(Collectors.toList());
    }

    public ServiceDto getServiceById(Long id) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        return new ServiceDto(service);
    }

    public ServiceDto createService(CreateServiceRequest request, Long userId, String userEmail, String ipAddress) {
        if (serviceRepository.findAll().stream().anyMatch(s -> s.getName().equalsIgnoreCase(request.getName()))) {
            throw new BadRequestException("Service name already exists: " + request.getName());
        }

        ServiceEntity service = new ServiceEntity();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setEnvironment(request.getEnvironment());
        service.setOwner(request.getOwner());
        service.setRepository(request.getRepository());
        service.setVersion(request.getVersion() != null ? request.getVersion() : "v1.0.0");
        service.setHealthEndpoint(request.getHealthEndpoint());
        service.setStatus(request.getStatus() != null ? request.getStatus() : ServiceStatus.HEALTHY);

        ServiceEntity saved = serviceRepository.save(service);
        auditLogService.logAction(userId, userEmail, "SERVICE_CREATE", "SERVICE", saved.getId().toString(), "Created service " + saved.getName(), ipAddress);

        return new ServiceDto(saved);
    }

    public ServiceDto updateService(Long id, CreateServiceRequest request, Long userId, String userEmail, String ipAddress) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setEnvironment(request.getEnvironment());
        service.setOwner(request.getOwner());
        service.setRepository(request.getRepository());
        if (request.getVersion() != null) service.setVersion(request.getVersion());
        service.setHealthEndpoint(request.getHealthEndpoint());
        if (request.getStatus() != null) service.setStatus(request.getStatus());

        ServiceEntity updated = serviceRepository.save(service);
        auditLogService.logAction(userId, userEmail, "SERVICE_UPDATE", "SERVICE", id.toString(), "Updated service " + updated.getName(), ipAddress);

        return new ServiceDto(updated);
    }

    public void updateServiceStatus(Long serviceId, ServiceStatus status) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        service.setStatus(status);
        serviceRepository.save(service);
    }

    public void deleteService(Long id, Long userId, String userEmail, String ipAddress) {
        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        serviceRepository.delete(service);
        auditLogService.logAction(userId, userEmail, "SERVICE_DELETE", "SERVICE", id.toString(), "Deleted service " + service.getName(), ipAddress);
    }
}
