package com.sentinelops.dto;

import com.sentinelops.domain.entity.AlertRule;
import com.sentinelops.domain.enums.IncidentSeverity;

public class AlertRuleDto {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private String metricName;
    private String comparisonOperator;
    private Double thresholdValue;
    private IncidentSeverity severity;
    private Boolean enabled;

    public AlertRuleDto() {}

    public AlertRuleDto(AlertRule rule) {
        this.id = rule.getId();
        this.serviceId = rule.getService().getId();
        this.serviceName = rule.getService().getName();
        this.metricName = rule.getMetricName();
        this.comparisonOperator = rule.getComparisonOperator();
        this.thresholdValue = rule.getThresholdValue();
        this.severity = rule.getSeverity();
        this.enabled = rule.getEnabled();
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

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(String comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public Double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
