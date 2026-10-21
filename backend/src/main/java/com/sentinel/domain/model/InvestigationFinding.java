package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "investigation_findings")
public class InvestigationFinding {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false, unique = true)
    @JsonIgnore
    private ProblemCase problemCase;

    @Column(name = "known_facts", columnDefinition = "TEXT", nullable = false)
    private String knownFacts;

    @Column(name = "assumptions", columnDefinition = "TEXT", nullable = false)
    private String assumptions;

    @Column(name = "missing_information", columnDefinition = "TEXT", nullable = false)
    private String missingInformation;

    @Column(name = "identified_constraints", columnDefinition = "TEXT", nullable = false)
    private String identifiedConstraints;

    @Column(name = "success_criteria", columnDefinition = "TEXT", nullable = false)
    private String successCriteria;

    @Column(name = "clarification_questions", columnDefinition = "TEXT")
    private String clarificationQuestions;

    @Column(name = "clarification_responses", columnDefinition = "TEXT")
    private String clarificationResponses;

    @Column(name = "diagnostic_summary", columnDefinition = "TEXT")
    private String diagnosticSummary;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "is_demo_output", nullable = false)
    private Boolean isDemoOutput = false;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    public InvestigationFinding() {
        this.id = UUID.randomUUID().toString();
        this.analyzedAt = LocalDateTime.now();
        this.isDemoOutput = false;
        this.knownFacts = "[]";
        this.assumptions = "[]";
        this.missingInformation = "[]";
        this.identifiedConstraints = "[]";
        this.successCriteria = "[]";
        this.clarificationQuestions = "[]";
    }

    public InvestigationFinding(ProblemCase problemCase) {
        this();
        this.problemCase = problemCase;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public String getKnownFacts() { return knownFacts; }
    public void setKnownFacts(String knownFacts) { this.knownFacts = knownFacts; }

    public String getAssumptions() { return assumptions; }
    public void setAssumptions(String assumptions) { this.assumptions = assumptions; }

    public String getMissingInformation() { return missingInformation; }
    public void setMissingInformation(String missingInformation) { this.missingInformation = missingInformation; }

    public String getIdentifiedConstraints() { return identifiedConstraints; }
    public void setIdentifiedConstraints(String identifiedConstraints) { this.identifiedConstraints = identifiedConstraints; }

    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }

    public String getClarificationQuestions() { return clarificationQuestions; }
    public void setClarificationQuestions(String clarificationQuestions) { this.clarificationQuestions = clarificationQuestions; }

    public String getClarificationResponses() { return clarificationResponses; }
    public void setClarificationResponses(String clarificationResponses) { this.clarificationResponses = clarificationResponses; }

    public String getDiagnosticSummary() { return diagnosticSummary; }
    public void setDiagnosticSummary(String diagnosticSummary) { this.diagnosticSummary = diagnosticSummary; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public Boolean getIsDemoOutput() { return isDemoOutput; }
    public void setIsDemoOutput(Boolean isDemoOutput) { this.isDemoOutput = isDemoOutput; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
