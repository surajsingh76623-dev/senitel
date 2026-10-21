package com.sentinel.dto;

import java.time.LocalDateTime;
import java.util.List;

public class InvestigationFindingDto {

    private String id;
    private String caseId;
    private List<String> knownFacts;
    private List<String> assumptions;
    private List<String> missingInformation;
    private List<String> identifiedConstraints;
    private List<String> successCriteria;
    private List<String> clarificationQuestions;
    private List<ClarificationAnswerRequest.ClarificationEntry> clarificationResponses;
    private String diagnosticSummary;
    private Double confidenceScore;
    private Boolean isDemoOutput;
    private LocalDateTime analyzedAt;

    public InvestigationFindingDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public List<String> getKnownFacts() { return knownFacts; }
    public void setKnownFacts(List<String> knownFacts) { this.knownFacts = knownFacts; }

    public List<String> getAssumptions() { return assumptions; }
    public void setAssumptions(List<String> assumptions) { this.assumptions = assumptions; }

    public List<String> getMissingInformation() { return missingInformation; }
    public void setMissingInformation(List<String> missingInformation) { this.missingInformation = missingInformation; }

    public List<String> getIdentifiedConstraints() { return identifiedConstraints; }
    public void setIdentifiedConstraints(List<String> identifiedConstraints) { this.identifiedConstraints = identifiedConstraints; }

    public List<String> getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(List<String> successCriteria) { this.successCriteria = successCriteria; }

    public List<String> getClarificationQuestions() { return clarificationQuestions; }
    public void setClarificationQuestions(List<String> clarificationQuestions) { this.clarificationQuestions = clarificationQuestions; }

    public List<ClarificationAnswerRequest.ClarificationEntry> getClarificationResponses() { return clarificationResponses; }
    public void setClarificationResponses(List<ClarificationAnswerRequest.ClarificationEntry> clarificationResponses) { this.clarificationResponses = clarificationResponses; }

    public String getDiagnosticSummary() { return diagnosticSummary; }
    public void setDiagnosticSummary(String diagnosticSummary) { this.diagnosticSummary = diagnosticSummary; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public Boolean getIsDemoOutput() { return isDemoOutput; }
    public void setIsDemoOutput(Boolean isDemoOutput) { this.isDemoOutput = isDemoOutput; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
