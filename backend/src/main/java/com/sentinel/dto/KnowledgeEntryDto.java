package com.sentinel.dto;

import java.time.LocalDateTime;

public class KnowledgeEntryDto {

    private String id;
    private String sourceCaseId;
    private String title;
    private String category;
    private String problemSummary;
    private String solutionSummary;
    private String verificationSummary;
    private String anonymizedLessons;
    private String tags;
    private Boolean isApproved;
    private String approvedBy;
    private LocalDateTime approvedAt;

    public KnowledgeEntryDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSourceCaseId() { return sourceCaseId; }
    public void setSourceCaseId(String sourceCaseId) { this.sourceCaseId = sourceCaseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProblemSummary() { return problemSummary; }
    public void setProblemSummary(String problemSummary) { this.problemSummary = problemSummary; }

    public String getSolutionSummary() { return solutionSummary; }
    public void setSolutionSummary(String solutionSummary) { this.solutionSummary = solutionSummary; }

    public String getVerificationSummary() { return verificationSummary; }
    public void setVerificationSummary(String verificationSummary) { this.verificationSummary = verificationSummary; }

    public String getAnonymizedLessons() { return anonymizedLessons; }
    public void setAnonymizedLessons(String anonymizedLessons) { this.anonymizedLessons = anonymizedLessons; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
