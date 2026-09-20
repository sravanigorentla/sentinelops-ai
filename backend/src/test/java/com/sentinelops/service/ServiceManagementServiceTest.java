package com.sentinelops.service;

import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.CreateServiceRequest;
import com.sentinelops.dto.ServiceDto;
import com.sentinelops.exception.BadRequestException;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceManagementServiceTest {

    @Mock private ServiceRepository serviceRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private ServiceManagementService serviceManagementService;

    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        service = new ServiceEntity();
        service.setId(1L);
        service.setName("inventory-service");
        service.setDescription("Manages inventory data");
        service.setEnvironment("production");
        service.setOwner("platform-team");
        service.setRepository("github.com/sentinelops/inventory-service");
        service.setVersion("v1.2.0");
        service.setStatus(ServiceStatus.HEALTHY);
    }

    @Test
    void testGetAllServices_ReturnsList() {
        when(serviceRepository.findAll()).thenReturn(List.of(service));

        List<ServiceDto> result = serviceManagementService.getAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("inventory-service", result.get(0).getName());
    }

    @Test
    void testGetServiceById_Found() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        ServiceDto result = serviceManagementService.getServiceById(1L);

        assertNotNull(result);
        assertEquals("inventory-service", result.getName());
        assertEquals(ServiceStatus.HEALTHY, result.getStatus());
    }

    @Test
    void testGetServiceById_NotFound_ThrowsException() {
        when(serviceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                serviceManagementService.getServiceById(999L));
    }

    @Test
    void testCreateService_Success() {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("notification-service");
        request.setDescription("Handles notifications");
        request.setEnvironment("staging");
        request.setOwner("backend-team");
        request.setRepository("github.com/sentinelops/notification-service");
        request.setVersion("v1.0.0");

        when(serviceRepository.findAll()).thenReturn(Collections.emptyList());

        ServiceEntity saved = new ServiceEntity();
        saved.setId(5L);
        saved.setName("notification-service");
        saved.setDescription("Handles notifications");
        saved.setEnvironment("staging");
        saved.setOwner("backend-team");
        saved.setVersion("v1.0.0");
        saved.setStatus(ServiceStatus.HEALTHY);
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(saved);

        ServiceDto result = serviceManagementService.createService(request, 1L, "admin@sentinelops.io", "127.0.0.1");

        assertNotNull(result);
        assertEquals("notification-service", result.getName());
        verify(auditLogService, times(1)).logAction(eq(1L), eq("admin@sentinelops.io"), eq("SERVICE_CREATE"), eq("SERVICE"), eq("5"), any(), eq("127.0.0.1"));
    }

    @Test
    void testCreateService_DuplicateName_ThrowsBadRequest() {
        CreateServiceRequest request = new CreateServiceRequest();
        request.setName("inventory-service");
        request.setEnvironment("production");
        request.setOwner("platform-team");

        when(serviceRepository.findAll()).thenReturn(List.of(service));

        assertThrows(BadRequestException.class, () ->
                serviceManagementService.createService(request, 1L, "admin@sentinelops.io", "127.0.0.1"));
    }

    @Test
    void testUpdateServiceStatus_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(i -> i.getArgument(0));

        serviceManagementService.updateServiceStatus(1L, ServiceStatus.DEGRADED);

        assertEquals(ServiceStatus.DEGRADED, service.getStatus());
        verify(serviceRepository, times(1)).save(service);
    }

    @Test
    void testDeleteService_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        serviceManagementService.deleteService(1L, 1L, "admin@sentinelops.io", "127.0.0.1");

        verify(serviceRepository, times(1)).delete(service);
        verify(auditLogService, times(1)).logAction(eq(1L), eq("admin@sentinelops.io"), eq("SERVICE_DELETE"), eq("SERVICE"), eq("1"), any(), eq("127.0.0.1"));
    }
}
