package com.sentinelops.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummaryDto {

    private long totalServices;
    private long healthyServices;
    private long degradedServices;
    private long downServices;
    private long activeIncidents;
    private long criticalIncidents;
    private double avgCpuUsage;
    private double avgMemoryUsage;
    private double totalRequestRate;
    private double avgLatencyMs;
    private double avgErrorRate;
    private List<ServiceDto> recentServices;
    private List<IncidentDto> recentIncidents;
    private List<DeploymentDto> recentDeployments;
    private Map<String, Long> incidentTrends;

    public DashboardSummaryDto() {}

    public long getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(long totalServices) {
        this.totalServices = totalServices;
    }

    public long getHealthyServices() {
        return healthyServices;
    }

    public void setHealthyServices(long healthyServices) {
        this.healthyServices = healthyServices;
    }

    public long getDegradedServices() {
        return degradedServices;
    }

    public void setDegradedServices(long degradedServices) {
        this.degradedServices = degradedServices;
    }

    public long getDownServices() {
        return downServices;
    }

    public void setDownServices(long downServices) {
        this.downServices = downServices;
    }

    public long getActiveIncidents() {
        return activeIncidents;
    }

    public void setActiveIncidents(long activeIncidents) {
        this.activeIncidents = activeIncidents;
    }

    public long getCriticalIncidents() {
        return criticalIncidents;
    }

    public void setCriticalIncidents(long criticalIncidents) {
        this.criticalIncidents = criticalIncidents;
    }

    public double getAvgCpuUsage() {
        return avgCpuUsage;
    }

    public void setAvgCpuUsage(double avgCpuUsage) {
        this.avgCpuUsage = avgCpuUsage;
    }

    public double getAvgMemoryUsage() {
        return avgMemoryUsage;
    }

    public void setAvgMemoryUsage(double avgMemoryUsage) {
        this.avgMemoryUsage = avgMemoryUsage;
    }

    public double getTotalRequestRate() {
        return totalRequestRate;
    }

    public void setTotalRequestRate(double totalRequestRate) {
        this.totalRequestRate = totalRequestRate;
    }

    public double getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public void setAvgLatencyMs(double avgLatencyMs) {
        this.avgLatencyMs = avgLatencyMs;
    }

    public double getAvgErrorRate() {
        return avgErrorRate;
    }

    public void setAvgErrorRate(double avgErrorRate) {
        this.avgErrorRate = avgErrorRate;
    }

    public List<ServiceDto> getRecentServices() {
        return recentServices;
    }

    public void setRecentServices(List<ServiceDto> recentServices) {
        this.recentServices = recentServices;
    }

    public List<IncidentDto> getRecentIncidents() {
        return recentIncidents;
    }

    public void setRecentIncidents(List<IncidentDto> recentIncidents) {
        this.recentIncidents = recentIncidents;
    }

    public List<DeploymentDto> getRecentDeployments() {
        return recentDeployments;
    }

    public void setRecentDeployments(List<DeploymentDto> recentDeployments) {
        this.recentDeployments = recentDeployments;
    }

    public Map<String, Long> getIncidentTrends() {
        return incidentTrends;
    }

    public void setIncidentTrends(Map<String, Long> incidentTrends) {
        this.incidentTrends = incidentTrends;
    }
}
