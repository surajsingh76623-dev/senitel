package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class DeterministicCheckRequest {

    @NotBlank(message = "Check type is required")
    private String checkType; // e.g. "PINN_L2_NORM", "SAAS_CHURN_MARGIN", "SQL_POOL_STARVATION"

    private String solutionId;

    private Map<String, Object> parameters;

    public DeterministicCheckRequest() {}

    public DeterministicCheckRequest(String checkType, String solutionId, Map<String, Object> parameters) {
        this.checkType = checkType;
        this.solutionId = solutionId;
        this.parameters = parameters;
    }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public String getSolutionId() { return solutionId; }
    public void setSolutionId(String solutionId) { this.solutionId = solutionId; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}
