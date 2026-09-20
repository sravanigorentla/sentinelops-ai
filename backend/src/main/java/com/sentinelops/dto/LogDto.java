package com.sentinelops.dto;

import com.sentinelops.domain.entity.LogEntity;
import com.sentinelops.domain.enums.LogLevel;

import java.time.ZonedDateTime;

public class LogDto {

    private Long id;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING)
    private ZonedDateTime timestamp;
    private Long serviceId;
    private String serviceName;
    private String environment;
    private LogLevel logLevel;
    private String message;
    private String requestId;
    private String metadata;

    public LogDto() {}

    public LogDto(LogEntity log) {
        this.id = log.getId();
        this.timestamp = log.getTimestamp();
        this.serviceId = log.getService().getId();
        this.serviceName = log.getService().getName();
        this.environment = log.getEnvironment();
        this.logLevel = log.getLogLevel();
        this.message = log.getMessage();
        this.requestId = log.getRequestId();
        this.metadata = log.getMetadata();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
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

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
