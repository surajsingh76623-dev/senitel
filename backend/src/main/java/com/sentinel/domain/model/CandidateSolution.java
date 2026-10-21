package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidate_solutions")
public class CandidateSolution {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private ProblemCase problemCase;

    @Column(name = "solution_index", nullable = false)
    private Integer solutionIndex;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "reasoning", columnDefinition = "TEXT", nullable = false)
    private String reasoning;

    @Column(name = "supporting_evidence", columnDefinition = "TEXT")
    private String supportingEvidence;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "tradeoffs", columnDefinition = "TEXT")
    private String tradeoffs;

    @Column(name = "risks", columnDefinition = "TEXT")
    private String risks;

    @Column(name = "effort_level", length = 20, nullable = false)
    private String effortLevel;

    @Column(name = "confidence_limitations", columnDefinition = "TEXT", nullable = false)
    private String confidenceLimitations;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CandidateSolution() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.isSelected = false;
        this.effortLevel = "MEDIUM";
        this.benefits = "[]";
        this.tradeoffs = "[]";
        this.risks = "[]";
        this.supportingEvidence = "[]";
    }

    public CandidateSolution(ProblemCase problemCase, Integer solutionIndex, String title, String summary, String reasoning, String effortLevel, String confidenceLimitations) {
        this();
        this.problemCase = problemCase;
        this.solutionIndex = solutionIndex;
        this.title = title;
        this.summary = summary;
        this.reasoning = reasoning;
        this.effortLevel = effortLevel != null ? effortLevel : "MEDIUM";
        this.confidenceLimitations = confidenceLimitations;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public Integer getSolutionIndex() { return solutionIndex; }
    public void setSolutionIndex(Integer solutionIndex) { this.solutionIndex = solutionIndex; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }

    public String getSupportingEvidence() { return supportingEvidence; }
    public void setSupportingEvidence(String supportingEvidence) { this.supportingEvidence = supportingEvidence; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getTradeoffs() { return tradeoffs; }
    public void setTradeoffs(String tradeoffs) { this.tradeoffs = tradeoffs; }

    public String getRisks() { return risks; }
    public void setRisks(String risks) { this.risks = risks; }

    public String getEffortLevel() { return effortLevel; }
    public void setEffortLevel(String effortLevel) { this.effortLevel = effortLevel; }

    public String getConfidenceLimitations() { return confidenceLimitations; }
    public void setConfidenceLimitations(String confidenceLimitations) { this.confidenceLimitations = confidenceLimitations; }

    public Boolean getIsSelected() { return isSelected; }
    public void setIsSelected(Boolean isSelected) { this.isSelected = isSelected; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
