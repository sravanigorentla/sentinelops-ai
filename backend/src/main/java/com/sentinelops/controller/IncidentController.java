package com.sentinelops.controller;

import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.dto.*;
import com.sentinelops.security.UserPrincipal;
import com.sentinelops.service.AIServiceClient;
import com.sentinelops.service.IncidentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final AIServiceClient aiServiceClient;

    public IncidentController(IncidentService incidentService, AIServiceClient aiServiceClient) {
        this.incidentService = incidentService;
        this.aiServiceClient = aiServiceClient;
    }

    @GetMapping
    public ResponseEntity<Page<IncidentDto>> filterIncidents(@RequestParam(required = false) Long serviceId,
                                                             @RequestParam(required = false) IncidentStatus status,
                                                             @RequestParam(required = false) IncidentSeverity severity,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(incidentService.filterIncidents(serviceId, status, severity, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDto> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncidentById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<IncidentDto> updateIncidentStatus(@PathVariable Long id,
                                                             @RequestBody UpdateIncidentRequest request,
                                                             @AuthenticationPrincipal UserPrincipal principal,
                                                             HttpServletRequest httpRequest) {
        return ResponseEntity.ok(incidentService.updateIncidentStatus(id, request, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }

    @PostMapping("/{id}/ai-analysis")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<AIAnalysisDto> triggerAIAnalysis(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserPrincipal principal,
                                                            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(aiServiceClient.analyzeIncident(id, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }

    @PostMapping("/{id}/rollback")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER')")
    public ResponseEntity<IncidentDto> triggerRollback(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal principal,
                                                         HttpServletRequest httpRequest) {
        return ResponseEntity.ok(incidentService.triggerDeploymentRollback(id, principal.getId(), principal.getUsername(), httpRequest.getRemoteAddr()));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<IncidentCommentDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getCommentsForIncident(id));
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DEVELOPER', 'ROLE_VIEWER')")
    public ResponseEntity<IncidentCommentDto> addComment(@PathVariable Long id,
                                                         @Valid @RequestBody AddCommentRequest request,
                                                         @AuthenticationPrincipal UserPrincipal principal,
                                                         HttpServletRequest httpRequest) {
        return ResponseEntity.ok(incidentService.addComment(id, request.getComment(), principal, httpRequest.getRemoteAddr()));
    }
}
