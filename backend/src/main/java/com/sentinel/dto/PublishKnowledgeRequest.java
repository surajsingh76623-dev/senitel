package com.sentinel.dto;

import jakarta.validation.constraints.NotBlank;

public class PublishKnowledgeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Problem summary is required")
    private String problemSummary;

    @NotBlank(message = "Solution summary is required")
    private String solutionSummary;

    @NotBlank(message = "Verification summary is required")
    private String verificationSummary;

    @NotBlank(message = "Anonymized lessons are required")
    private String anonymizedLessons;

    private String tags;

    @NotBlank(message = "Approved by author/reviewer is required")
    private String approvedBy = "User";

    public PublishKnowledgeRequest() {}

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

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}
