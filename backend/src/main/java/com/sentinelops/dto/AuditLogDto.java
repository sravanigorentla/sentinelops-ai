package com.sentinelops.dto;

import com.sentinelops.domain.entity.AuditLogEntity;
import java.time.ZonedDateTime;

public class AuditLogDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private String action;
    private String targetType;
    private String targetId;
    private String details;
    private String ipAddress;
    private ZonedDateTime timestamp;

    public AuditLogDto() {}

    public AuditLogDto(AuditLogEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.userEmail = entity.getUserEmail();
        this.action = entity.getAction();
        this.targetType = entity.getTargetType();
        this.targetId = entity.getTargetId();
        this.details = entity.getDetails();
        this.ipAddress = entity.getIpAddress();
        this.timestamp = entity.getTimestamp();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
