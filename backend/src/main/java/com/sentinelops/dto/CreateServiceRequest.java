package com.sentinelops.dto;

import com.sentinelops.domain.enums.ServiceStatus;
import jakarta.validation.constraints.NotBlank;

public class CreateServiceRequest {

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    @NotBlank(message = "Environment is required")
    private String environment;

    @NotBlank(message = "Owner is required")
    private String owner;

    private String repository;

    private String version;

    private String healthEndpoint;

    private ServiceStatus status = ServiceStatus.HEALTHY;

    public CreateServiceRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHealthEndpoint() {
        return healthEndpoint;
    }

    public void setHealthEndpoint(String healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }
}
