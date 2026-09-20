package com.sentinelops.dto;

import com.sentinelops.domain.entity.NotificationEntity;
import java.time.ZonedDateTime;

public class NotificationDto {

    private Long id;
    private Long incidentId;
    private String channel;
    private String recipient;
    private String status;
    private String message;
    private ZonedDateTime sentAt;

    public NotificationDto() {}

    public NotificationDto(NotificationEntity notification) {
        this.id = notification.getId();
        if (notification.getIncident() != null) {
            this.incidentId = notification.getIncident().getId();
        }
        this.channel = notification.getChannel();
        this.recipient = notification.getRecipient();
        this.status = notification.getStatus();
        this.message = notification.getMessage();
        this.sentAt = notification.getSentAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(ZonedDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
