package com.sentinelops.service;

import com.sentinelops.domain.entity.*;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.domain.enums.RollbackStatus;
import com.sentinelops.domain.enums.ServiceStatus;
import com.sentinelops.dto.*;
import com.sentinelops.exception.ResourceNotFoundException;
import com.sentinelops.repository.*;
import com.sentinelops.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentCommentRepository commentRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final DeploymentRepository deploymentRepository;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final AuditLogService auditLogService;

    public IncidentService(IncidentRepository incidentRepository,
                           IncidentCommentRepository commentRepository,
                           ServiceRepository serviceRepository,
                           UserRepository userRepository,
                           DeploymentRepository deploymentRepository,
                           AIAnalysisRepository aiAnalysisRepository,
                           AuditLogService auditLogService) {
        this.incidentRepository = incidentRepository;
        this.commentRepository = commentRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.deploymentRepository = deploymentRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.auditLogService = auditLogService;
    }

    public Page<IncidentDto> filterIncidents(Long serviceId, IncidentStatus status, IncidentSeverity severity, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("detectedAt").descending());
        return incidentRepository.filterIncidents(serviceId, status, severity, pageable)
                .map(this::mapToDtoWithAI);
    }

    public IncidentDto getIncidentById(Long id) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));
        return mapToDtoWithAI(incident);
    }

    @Transactional
    public IncidentEntity triggerAutomaticIncident(ServiceEntity service, String title, String description, IncidentSeverity severity) {
        // Check if active incident already exists for service
        List<IncidentEntity> activeIncidents = incidentRepository.findByServiceIdAndStatusNot(service.getId(), IncidentStatus.RESOLVED);
        if (!activeIncidents.isEmpty()) {
            return activeIncidents.get(0);
        }

        IncidentEntity incident = new IncidentEntity();
        incident.setTitle(title);
        incident.setDescription(description);
        incident.setSeverity(severity);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setService(service);
        incident.setDetectedAt(ZonedDateTime.now());

        // Correlate with recent deployment in last 2 hours
        ZonedDateTime twoHoursAgo = ZonedDateTime.now().minusHours(2);
        List<DeploymentEntity> recentDeployments = deploymentRepository
                .findByServiceIdAndDeploymentTimeAfterOrderByDeploymentTimeDesc(service.getId(), twoHoursAgo);

        if (!recentDeployments.isEmpty()) {
            incident.setDeployment(recentDeployments.get(0));
            incident.setRollbackStatus(RollbackStatus.PENDING);
        }

        return incidentRepository.save(incident);
    }

    @Transactional
    public IncidentDto updateIncidentStatus(Long id, UpdateIncidentRequest request, Long userId, String userEmail, String ipAddress) {
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + id));

        if (request.getStatus() != null) {
            incident.setStatus(request.getStatus());
            if (request.getStatus() == IncidentStatus.ACKNOWLEDGED && incident.getAcknowledgedAt() == null) {
                incident.setAcknowledgedAt(ZonedDateTime.now());
            } else if (request.getStatus() == IncidentStatus.RESOLVED) {
                incident.setResolvedAt(ZonedDateTime.now());
                // Restore service status to HEALTHY if all incidents resolved
                ServiceEntity service = incident.getService();
                List<IncidentEntity> remainingActive = incidentRepository.findByServiceIdAndStatusNot(service.getId(), IncidentStatus.RESOLVED);
                if (remainingActive.size() <= 1) {
                    service.setStatus(ServiceStatus.HEALTHY);
                    serviceRepository.save(service);
                }
            }
        }

        if (request.getSeverity() != null) incident.setSeverity(request.getSeverity());
        if (request.getRootCause() != null) incident.setRootCause(request.getRootCause());
        if (request.getResolution() != null) incident.setResolution(request.getResolution());

        if (request.getAssignedToUserId() != null) {
            User user = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getAssignedToUserId()));
            incident.setAssignedTo(user);
        }

        IncidentEntity saved = incidentRepository.save(incident);
        auditLogService.logAction(userId, userEmail, "INCIDENT_UPDATE", "INCIDENT", id.toString(),
                "Updated status to " + saved.getStatus(), ipAddress);

        return mapToDtoWithAI(saved);
    }

    @Transactional
    public IncidentDto triggerDeploymentRollback(Long incidentId, Long userId, String userEmail, String ipAddress) {
        IncidentEntity incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));

        if (incident.getDeployment() == null) {
            throw new ResourceNotFoundException("No deployment correlated with incident #" + incidentId);
        }

        DeploymentEntity deployment = incident.getDeployment();
        deployment.setRolledBack(true);
        deployment.setRolledBackAt(ZonedDateTime.now());
        deploymentRepository.save(deployment);

        incident.setRollbackStatus(RollbackStatus.COMPLETED);
        incident.setResolution("Automated deployment rollback executed for commit " + deployment.getCommitSha().substring(0, 7) + " (" + deployment.getVersion() + ").");
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(ZonedDateTime.now());

        // Restore Service status
        ServiceEntity service = incident.getService();
        service.setStatus(ServiceStatus.HEALTHY);
        serviceRepository.save(service);

        IncidentEntity saved = incidentRepository.save(incident);

        auditLogService.logAction(userId, userEmail, "DEPLOYMENT_ROLLBACK", "DEPLOYMENT", deployment.getId().toString(),
                "Executed rollback for deployment " + deployment.getVersion() + " on incident #" + incidentId, ipAddress);

        return mapToDtoWithAI(saved);
    }

    public IncidentCommentDto addComment(Long incidentId, String commentText, UserPrincipal principal, String ipAddress) {
        IncidentEntity incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with ID: " + incidentId));
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        IncidentComment comment = new IncidentComment();
        comment.setIncident(incident);
        comment.setUser(user);
        comment.setComment(commentText);

        IncidentComment saved = commentRepository.save(comment);
        auditLogService.logAction(principal.getId(), principal.getUsername(), "INCIDENT_COMMENT", "INCIDENT", incidentId.toString(),
                "Added comment", ipAddress);

        return new IncidentCommentDto(saved);
    }

    public List<IncidentCommentDto> getCommentsForIncident(Long incidentId) {
        return commentRepository.findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream().map(IncidentCommentDto::new).toList();
    }

    private IncidentDto mapToDtoWithAI(IncidentEntity incident) {
        IncidentDto dto = new IncidentDto(incident);
        aiAnalysisRepository.findByIncidentId(incident.getId()).ifPresent(ai -> {
            List<String> evidence = parseJsonList(ai.getEvidenceJson());
            List<String> recommendedActions = parseJsonList(ai.getRecommendedActionsJson());
            List<String> relatedSignals = parseJsonList(ai.getRelatedSignalsJson());
            dto.setAiAnalysis(new AIAnalysisDto(ai, evidence, recommendedActions, relatedSignals));
        });
        return dto;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of(json);
        }
    }
}
