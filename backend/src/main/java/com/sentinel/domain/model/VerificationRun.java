package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.enums.VerificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_runs")
public class VerificationRun {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private ProblemCase problemCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id")
    @JsonIgnore
    private CandidateSolution candidateSolution;

    @Column(name = "test_procedure", columnDefinition = "TEXT", nullable = false)
    private String testProcedure;

    @Column(name = "expected_result", columnDefinition = "TEXT", nullable = false)
    private String expectedResult;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VerificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", length = 30, nullable = false)
    private VerificationType verificationType;

    @Column(name = "evidence_notes", columnDefinition = "TEXT")
    private String evidenceNotes;

    @Column(name = "recorded_by", length = 100, nullable = false)
    private String recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "is_reproducible", nullable = false)
    private Boolean isReproducible = true;

    public VerificationRun() {
        this.id = UUID.randomUUID().toString();
        this.recordedAt = LocalDateTime.now();
        this.status = VerificationStatus.PENDING;
        this.verificationType = VerificationType.MANUAL;
        this.isReproducible = true;
    }

    public VerificationRun(ProblemCase problemCase, CandidateSolution candidateSolution, String testProcedure, String expectedResult, VerificationType verificationType, String recordedBy) {
        this();
        this.problemCase = problemCase;
        this.candidateSolution = candidateSolution;
        this.testProcedure = testProcedure;
        this.expectedResult = expectedResult;
        this.verificationType = verificationType != null ? verificationType : VerificationType.MANUAL;
        this.recordedBy = recordedBy != null ? recordedBy : "User";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public CandidateSolution getCandidateSolution() { return candidateSolution; }
    public void setCandidateSolution(CandidateSolution candidateSolution) { this.candidateSolution = candidateSolution; }

    public String getTestProcedure() { return testProcedure; }
    public void setTestProcedure(String testProcedure) { this.testProcedure = testProcedure; }

    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }

    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }

    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }

    public VerificationType getVerificationType() { return verificationType; }
    public void setVerificationType(VerificationType verificationType) { this.verificationType = verificationType; }

    public String getEvidenceNotes() { return evidenceNotes; }
    public void setEvidenceNotes(String evidenceNotes) { this.evidenceNotes = evidenceNotes; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Boolean getIsReproducible() { return isReproducible; }
    public void setIsReproducible(Boolean isReproducible) { this.isReproducible = isReproducible; }
}
