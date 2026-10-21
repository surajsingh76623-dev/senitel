package com.sentinel.dto;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.ProblemUrgency;
import java.time.LocalDateTime;

public class ProblemSummaryDto {

    private String id;
    private String title;
    private String description;
    private ProblemCategory category;
    private String categoryDisplayName;
    private String goal;
    private ProblemUrgency urgency;
    private ProblemStatus status;
    private String statusDisplayName;
    private int evidenceCount;
    private int solutionsCount;
    private int actionItemsCount;
    private int completedActionItemsCount;
    private int verificationRunsCount;
    private boolean hasVerifiedRun;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProblemSummaryDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProblemCategory getCategory() { return category; }
    public void setCategory(ProblemCategory category) {
        this.category = category;
        this.categoryDisplayName = category != null ? category.getDisplayName() : null;
    }

    public String getCategoryDisplayName() { return categoryDisplayName; }
    public void setCategoryDisplayName(String categoryDisplayName) { this.categoryDisplayName = categoryDisplayName; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public ProblemUrgency getUrgency() { return urgency; }
    public void setUrgency(ProblemUrgency urgency) { this.urgency = urgency; }

    public ProblemStatus getStatus() { return status; }
    public void setStatus(ProblemStatus status) {
        this.status = status;
        this.statusDisplayName = status != null ? status.getDisplayName() : null;
    }

    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }

    public int getEvidenceCount() { return evidenceCount; }
    public void setEvidenceCount(int evidenceCount) { this.evidenceCount = evidenceCount; }

    public int getSolutionsCount() { return solutionsCount; }
    public void setSolutionsCount(int solutionsCount) { this.solutionsCount = solutionsCount; }

    public int getActionItemsCount() { return actionItemsCount; }
    public void setActionItemsCount(int actionItemsCount) { this.actionItemsCount = actionItemsCount; }

    public int getCompletedActionItemsCount() { return completedActionItemsCount; }
    public void setCompletedActionItemsCount(int completedActionItemsCount) { this.completedActionItemsCount = completedActionItemsCount; }

    public int getVerificationRunsCount() { return verificationRunsCount; }
    public void setVerificationRunsCount(int verificationRunsCount) { this.verificationRunsCount = verificationRunsCount; }

    public boolean isHasVerifiedRun() { return hasVerifiedRun; }
    public void setHasVerifiedRun(boolean hasVerifiedRun) { this.hasVerifiedRun = hasVerifiedRun; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
