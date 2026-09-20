package com.sentinelops.dto;

import com.sentinelops.domain.entity.ServiceEntity;
import com.sentinelops.domain.enums.ServiceStatus;

import java.time.ZonedDateTime;

public class ServiceDto {

    private Long id;
    private String name;
    private String description;
    private String environment;
    private String owner;
    private String repository;
    private String version;
    private String healthEndpoint;
    private ServiceStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public ServiceDto() {}

    public ServiceDto(ServiceEntity service) {
        this.id = service.getId();
        this.name = service.getName();
        this.description = service.getDescription();
        this.environment = service.getEnvironment();
        this.owner = service.getOwner();
        this.repository = service.getRepository();
        this.version = service.getVersion();
        this.healthEndpoint = service.getHealthEndpoint();
        this.status = service.getStatus();
        this.createdAt = service.getCreatedAt();
        this.updatedAt = service.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
