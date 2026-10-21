package com.sentinel.dto;

import com.sentinel.domain.enums.VerificationStatus;
import java.util.Map;

public class DeterministicCheckResponse {

    private boolean passed;
    private VerificationStatus status;
    private String checkName;
    private String expectedCondition;
    private Object actualValue;
    private String calculationDetails;
    private String testProcedure;
    private String evidenceNotes;
    private Map<String, Object> outputMetrics;

    public DeterministicCheckResponse() {}

    public DeterministicCheckResponse(boolean passed, VerificationStatus status, String checkName, String expectedCondition, Object actualValue, String calculationDetails, String testProcedure, String evidenceNotes, Map<String, Object> outputMetrics) {
        this.passed = passed;
        this.status = status;
        this.checkName = checkName;
        this.expectedCondition = expectedCondition;
        this.actualValue = actualValue;
        this.calculationDetails = calculationDetails;
        this.testProcedure = testProcedure;
        this.evidenceNotes = evidenceNotes;
        this.outputMetrics = outputMetrics;
    }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }

    public String getCheckName() { return checkName; }
    public void setCheckName(String checkName) { this.checkName = checkName; }

    public String getExpectedCondition() { return expectedCondition; }
    public void setExpectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; }

    public Object getActualValue() { return actualValue; }
    public void setActualValue(Object actualValue) { this.actualValue = actualValue; }

    public String getCalculationDetails() { return calculationDetails; }
    public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }

    public String getTestProcedure() { return testProcedure; }
    public void setTestProcedure(String testProcedure) { this.testProcedure = testProcedure; }

    public String getEvidenceNotes() { return evidenceNotes; }
    public void setEvidenceNotes(String evidenceNotes) { this.evidenceNotes = evidenceNotes; }

    public Map<String, Object> getOutputMetrics() { return outputMetrics; }
    public void setOutputMetrics(Map<String, Object> outputMetrics) { this.outputMetrics = outputMetrics; }
}
