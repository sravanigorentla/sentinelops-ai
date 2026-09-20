package com.sentinelops.controller;

import com.sentinelops.dto.CreateDeploymentRequest;
import com.sentinelops.dto.DeploymentDto;
import com.sentinelops.security.UserPrincipal;
import com.sentinelops.service.DeploymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @GetMapping
    public ResponseEntity<List<DeploymentDto>> getAllDeployments() {
        return ResponseEntity.ok(deploymentService.getAllDeployments());
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<DeploymentDto>> getDeploymentsForService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(deploymentService.getDeploymentsForService(serviceId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<DeploymentDto> createDeployment(@Valid @RequestBody CreateDeploymentRequest request,
                                                           @AuthenticationPrincipal UserPrincipal principal,
                                                           HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deploymentService.createDeployment(request, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }
}
