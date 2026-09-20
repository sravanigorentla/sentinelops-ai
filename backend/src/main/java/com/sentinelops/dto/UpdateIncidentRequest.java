package com.sentinelops.dto;

import com.sentinelops.domain.enums.IncidentSeverity;
import com.sentinelops.domain.enums.IncidentStatus;

public class UpdateIncidentRequest {

    private IncidentStatus status;
    private IncidentSeverity severity;
    private Long assignedToUserId;
    private String rootCause;
    private String resolution;

    public UpdateIncidentRequest() {}

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
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
}
