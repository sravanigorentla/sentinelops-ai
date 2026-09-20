package com.sentinelops.dto;

import com.sentinelops.domain.entity.AIAnalysisEntity;
import java.time.ZonedDateTime;
import java.util.List;

public class AIAnalysisDto {

    private Long id;
    private Long incidentId;
    private String summary;
    private String probableRootCause;
    private List<String> evidence;
    private List<String> recommendedActions;
    private List<String> relatedSignals;
    private Double confidence;
    private ZonedDateTime generatedAt;
    private UserDto verifiedBy;

    public AIAnalysisDto() {}

    public AIAnalysisDto(AIAnalysisEntity entity, List<String> evidence, List<String> recommendedActions, List<String> relatedSignals) {
        this.id = entity.getId();
        this.incidentId = entity.getIncident().getId();
        this.summary = entity.getSummary();
        this.probableRootCause = entity.getProbableRootCause();
        this.evidence = evidence;
        this.recommendedActions = recommendedActions;
        this.relatedSignals = relatedSignals;
        this.confidence = entity.getConfidence();
        this.generatedAt = entity.getGeneratedAt();
        if (entity.getVerifiedBy() != null) {
            this.verifiedBy = new UserDto(entity.getVerifiedBy());
        }
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getProbableRootCause() {
        return probableRootCause;
    }

    public void setProbableRootCause(String probableRootCause) {
        this.probableRootCause = probableRootCause;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(List<String> recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public List<String> getRelatedSignals() {
        return relatedSignals;
    }

    public void setRelatedSignals(List<String> relatedSignals) {
        this.relatedSignals = relatedSignals;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public ZonedDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(ZonedDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public UserDto getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(UserDto verifiedBy) {
        this.verifiedBy = verifiedBy;
    }
}
