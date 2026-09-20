package com.sentinelops.dto;

import com.sentinelops.domain.entity.IncidentEntity;
import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;
import com.sentinelops.domain.enums.RollbackStatus;

import java.time.ZonedDateTime;

public class IncidentDto {

    private Long id;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private IncidentStatus status;
    private Long serviceId;
    private String serviceName;
    private ZonedDateTime detectedAt;
    private ZonedDateTime acknowledgedAt;
    private ZonedDateTime resolvedAt;
    private UserDto assignedTo;
    private String rootCause;
    private String resolution;
    private DeploymentDto deployment;
    private RollbackStatus rollbackStatus;
    private AIAnalysisDto aiAnalysis;

    public IncidentDto() {}

    public IncidentDto(IncidentEntity incident) {
        this.id = incident.getId();
        this.title = incident.getTitle();
        this.description = incident.getDescription();
        this.severity = incident.getSeverity();
        this.status = incident.getStatus();
        this.serviceId = incident.getService().getId();
        this.serviceName = incident.getService().getName();
        this.detectedAt = incident.getDetectedAt();
        this.acknowledgedAt = incident.getAcknowledgedAt();
        this.resolvedAt = incident.getResolvedAt();
        if (incident.getAssignedTo() != null) {
            this.assignedTo = new UserDto(incident.getAssignedTo());
        }
        this.rootCause = incident.getRootCause();
        this.resolution = incident.getResolution();
        if (incident.getDeployment() != null) {
            this.deployment = new DeploymentDto(incident.getDeployment());
        }
        this.rollbackStatus = incident.getRollbackStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ZonedDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(ZonedDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public ZonedDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(ZonedDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public ZonedDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public UserDto getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserDto assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public DeploymentDto getDeployment() {
        return deployment;
    }

    public void setDeployment(DeploymentDto deployment) {
        this.deployment = deployment;
    }

    public RollbackStatus getRollbackStatus() {
        return rollbackStatus;
    }

    public void setRollbackStatus(RollbackStatus rollbackStatus) {
        this.rollbackStatus = rollbackStatus;
    }

    public AIAnalysisDto getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(AIAnalysisDto aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }
}
