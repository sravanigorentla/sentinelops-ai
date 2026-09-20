package com.sentinelops.dto;

import jakarta.validation.constraints.NotNull;

public class SimulateIncidentRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String simulationType; // DB_FAILURE, MEMORY_LEAK, HIGH_LATENCY, HIGH_ERROR_RATE

    public SimulateIncidentRequest() {}

    public SimulateIncidentRequest(Long serviceId, String simulationType) {
        this.serviceId = serviceId;
        this.simulationType = simulationType;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getSimulationType() {
        return simulationType;
    }

    public void setSimulationType(String simulationType) {
        this.simulationType = simulationType;
    }
}
