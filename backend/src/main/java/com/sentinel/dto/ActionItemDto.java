package com.sentinel.dto;

import java.time.LocalDateTime;

public class ActionItemDto {

    private String id;
    private String caseId;
    private String solutionId;
    private Integer stepNumber;
    private String title;
    private String description;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public ActionItemDto() {}

    public ActionItemDto(String id, String caseId, String solutionId, Integer stepNumber, String title, String description, Boolean isCompleted, LocalDateTime completedAt, LocalDateTime createdAt) {
        this.id = id;
        this.caseId = caseId;
        this.solutionId = solutionId;
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getSolutionId() { return solutionId; }
    public void setSolutionId(String solutionId) { this.solutionId = solutionId; }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
