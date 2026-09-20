package com.sentinelops.domain.entity;

import com.sentinelops.domain.enums.DeploymentStatus;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "deployments")
public class DeploymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(name = "commit_sha", nullable = false, length = 40)
    private String commitSha;

    @Column(nullable = false, length = 100)
    private String branch;

    @Column(nullable = false, length = 50)
    private String environment;

    @Column(name = "deployed_by", nullable = false, length = 100)
    private String deployedBy;

    @Column(name = "deployment_time", nullable = false)
    private ZonedDateTime deploymentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeploymentStatus status = DeploymentStatus.SUCCESS;

    @Column(name = "rolled_back")
    private Boolean rolledBack = false;

    @Column(name = "rolled_back_at")
    private ZonedDateTime rolledBackAt;

    public DeploymentEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
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
