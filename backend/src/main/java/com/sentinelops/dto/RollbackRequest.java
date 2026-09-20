package com.sentinelops.dto;

public class RollbackRequest {

    private String reason;
    private String targetVersion;

    public RollbackRequest() {}

    public RollbackRequest(String reason, String targetVersion) {
        this.reason = reason;
        this.targetVersion = targetVersion;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }
}
