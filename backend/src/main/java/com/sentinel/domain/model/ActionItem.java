package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "action_items")
public class ActionItem {

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

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ActionItem() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.isCompleted = false;
    }

    public ActionItem(ProblemCase problemCase, CandidateSolution candidateSolution, Integer stepNumber, String title, String description) {
        this();
        this.problemCase = problemCase;
        this.candidateSolution = candidateSolution;
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public CandidateSolution getCandidateSolution() { return candidateSolution; }
    public void setCandidateSolution(CandidateSolution candidateSolution) { this.candidateSolution = candidateSolution; }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
        this.completedAt = isCompleted ? LocalDateTime.now() : null;
    }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
