package com.sentinelops.dto;

import com.sentinelops.domain.entity.IncidentComment;
import java.time.ZonedDateTime;

public class IncidentCommentDto {

    private Long id;
    private Long incidentId;
    private UserDto user;
    private String comment;
    private ZonedDateTime createdAt;

    public IncidentCommentDto() {}

    public IncidentCommentDto(IncidentComment comment) {
        this.id = comment.getId();
        this.incidentId = comment.getIncident().getId();
        this.user = new UserDto(comment.getUser());
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
