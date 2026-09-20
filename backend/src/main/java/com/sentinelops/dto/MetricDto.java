package com.sentinelops.dto;

import com.sentinelops.domain.entity.MetricEntity;
import java.time.ZonedDateTime;

public class MetricDto {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double requestRate;
    private Double latencyMs;
    private Double errorRate;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private ZonedDateTime timestamp;

    public MetricDto() {}

    public MetricDto(MetricEntity metric) {
        this.id = metric.getId();
        this.serviceId = metric.getService().getId();
        this.serviceName = metric.getService().getName();
        this.cpuUsage = metric.getCpuUsage();
        this.memoryUsage = metric.getMemoryUsage();
        this.requestRate = metric.getRequestRate();
        this.latencyMs = metric.getLatencyMs();
        this.errorRate = metric.getErrorRate();
        this.timestamp = metric.getTimestamp();
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

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public Double getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(Double requestRate) {
        this.requestRate = requestRate;
    }

    public Double getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Double latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
