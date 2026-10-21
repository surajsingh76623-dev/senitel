package com.sentinel.dto;

import com.sentinel.domain.enums.ProblemStatus;
import jakarta.validation.constraints.NotNull;

public class StatusTransitionRequest {

    @NotNull(message = "Target status is required")
    private ProblemStatus targetStatus;

    private String reason;
    private String actor = "User";

    public StatusTransitionRequest() {}

    public StatusTransitionRequest(ProblemStatus targetStatus, String reason, String actor) {
        this.targetStatus = targetStatus;
        this.reason = reason;
        this.actor = actor != null ? actor : "User";
    }

    public ProblemStatus getTargetStatus() { return targetStatus; }
    public void setTargetStatus(ProblemStatus targetStatus) { this.targetStatus = targetStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
}
