package com.sentinelops.controller;

import com.sentinelops.dto.CreateServiceRequest;
import com.sentinelops.dto.ServiceDto;
import com.sentinelops.security.UserPrincipal;
import com.sentinelops.service.ServiceManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    public ServiceController(ServiceManagementService serviceManagementService) {
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceManagementService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.getServiceById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody CreateServiceRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal,
                                                     HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceManagementService.createService(request, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id,
                                                     @Valid @RequestBody CreateServiceRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal,
                                                     HttpServletRequest httpRequest) {
        return ResponseEntity.ok(serviceManagementService.updateService(id, request, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id,
                                               @AuthenticationPrincipal UserPrincipal principal,
                                               HttpServletRequest httpRequest) {
        serviceManagementService.deleteService(id, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
