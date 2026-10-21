package com.sentinel.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CandidateSolutionDto {

    private String id;
    private String caseId;
    private Integer solutionIndex;
    private String title;
    private String summary;
    private String reasoning;
    private List<String> supportingEvidence;
    private List<String> benefits;
    private List<String> tradeoffs;
    private List<String> risks;
    private String effortLevel;
    private String confidenceLimitations;
    private Boolean isSelected;
    private LocalDateTime createdAt;

    public CandidateSolutionDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public Integer getSolutionIndex() { return solutionIndex; }
    public void setSolutionIndex(Integer solutionIndex) { this.solutionIndex = solutionIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public List<String> getSupportingEvidence() { return supportingEvidence; }
    public void setSupportingEvidence(List<String> supportingEvidence) { this.supportingEvidence = supportingEvidence; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public List<String> getTradeoffs() { return tradeoffs; }
    public void setTradeoffs(List<String> tradeoffs) { this.tradeoffs = tradeoffs; }

    public List<String> getRisks() { return risks; }
    public void setRisks(List<String> risks) { this.risks = risks; }

    public String getEffortLevel() { return effortLevel; }
    public void setEffortLevel(String effortLevel) { this.effortLevel = effortLevel; }

    public String getConfidenceLimitations() { return confidenceLimitations; }
    public void setConfidenceLimitations(String confidenceLimitations) { this.confidenceLimitations = confidenceLimitations; }

    public Boolean getIsSelected() { return isSelected; }
    public void setIsSelected(Boolean isSelected) { this.isSelected = isSelected; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
