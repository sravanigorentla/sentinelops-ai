package com.sentinelops.dto;

import java.util.List;

public class AIAnalysisRequest {

    private Long incidentId;
    private String title;
    private String description;
    private String severity;
    private String serviceName;
    private String environment;
    private List<LogDto> recentLogs;
    private List<MetricDto> recentMetrics;
    private DeploymentDto recentDeployment;

    public AIAnalysisRequest() {}

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public List<LogDto> getRecentLogs() {
        return recentLogs;
    }

    public void setRecentLogs(List<LogDto> recentLogs) {
        this.recentLogs = recentLogs;
    }

    public List<MetricDto> getRecentMetrics() {
        return recentMetrics;
    }

    public void setRecentMetrics(List<MetricDto> recentMetrics) {
        this.recentMetrics = recentMetrics;
    }

    public DeploymentDto getRecentDeployment() {
        return recentDeployment;
    }

    public void setRecentDeployment(DeploymentDto recentDeployment) {
        this.recentDeployment = recentDeployment;
    }
}
