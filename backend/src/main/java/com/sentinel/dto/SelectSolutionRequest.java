package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;

public class SelectSolutionRequest {

    @NotBlank(message = "Solution ID is required")
    private String solutionId;

    public SelectSolutionRequest() {}

    public SelectSolutionRequest(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getSolutionId() { return solutionId; }
    public void setSolutionId(String solutionId) { this.solutionId = solutionId; }
}
