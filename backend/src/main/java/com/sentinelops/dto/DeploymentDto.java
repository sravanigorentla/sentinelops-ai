package com.sentinelops.dto;

import com.sentinelops.domain.entity.DeploymentEntity;
import com.sentinelops.domain.enums.DeploymentStatus;

import java.time.ZonedDateTime;

public class DeploymentDto {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private String version;
    private String commitSha;
    private String branch;
    private String environment;
    private String deployedBy;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private ZonedDateTime deploymentTime;
    private DeploymentStatus status;
    private Boolean rolledBack;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private ZonedDateTime rolledBackAt;

    public DeploymentDto() {}

    public DeploymentDto(DeploymentEntity deployment) {
        this.id = deployment.getId();
        this.serviceId = deployment.getService().getId();
        this.serviceName = deployment.getService().getName();
        this.version = deployment.getVersion();
        this.commitSha = deployment.getCommitSha();
        this.branch = deployment.getBranch();
        this.environment = deployment.getEnvironment();
        this.deployedBy = deployment.getDeployedBy();
        this.deploymentTime = deployment.getDeploymentTime();
        this.status = deployment.getStatus();
        this.rolledBack = deployment.getRolledBack();
        this.rolledBackAt = deployment.getRolledBackAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy;
    }

    public ZonedDateTime getDeploymentTime() {
        return deploymentTime;
    }

    public void setDeploymentTime(ZonedDateTime deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public void setStatus(DeploymentStatus status) {
        this.status = status;
    }

    public Boolean getRolledBack() {
        return rolledBack;
    }

    public void setRolledBack(Boolean rolledBack) {
        this.rolledBack = rolledBack;
    }

    public ZonedDateTime getRolledBackAt() {
        return rolledBackAt;
    }

    public void setRolledBackAt(ZonedDateTime rolledBackAt) {
        this.rolledBackAt = rolledBackAt;
    }
}
