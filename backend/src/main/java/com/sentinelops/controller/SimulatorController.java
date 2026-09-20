package com.sentinelops.controller;

import com.sentinelops.dto.IncidentDto;
import com.sentinelops.dto.SimulateIncidentRequest;
import com.sentinelops.security.UserPrincipal;
import com.sentinelops.service.IncidentSimulatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulator")
public class SimulatorController {

    private final IncidentSimulatorService simulatorService;

    public SimulatorController(IncidentSimulatorService simulatorService) {
        this.simulatorService = simulatorService;
    }

    @PostMapping("/trigger")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<IncidentDto> simulateIncident(@Valid @RequestBody SimulateIncidentRequest request,
                                                         @AuthenticationPrincipal UserPrincipal principal,
                                                         HttpServletRequest httpRequest) {
        return ResponseEntity.ok(simulatorService.simulateIncident(request, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }
}
