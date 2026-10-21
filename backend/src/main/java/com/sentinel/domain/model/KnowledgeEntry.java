package com.sentinel.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "knowledge_entries")
public class KnowledgeEntry {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "source_case_id", length = 36)
    private String sourceCaseId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "problem_summary", columnDefinition = "TEXT", nullable = false)
    private String problemSummary;

    @Column(name = "solution_summary", columnDefinition = "TEXT", nullable = false)
    private String solutionSummary;

    @Column(name = "verification_summary", columnDefinition = "TEXT", nullable = false)
    private String verificationSummary;

    @Column(name = "anonymized_lessons", columnDefinition = "TEXT", nullable = false)
    private String anonymizedLessons;

    @Column(name = "tags", length = 255)
    private String tags;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "approved_by", length = 100, nullable = false)
    private String approvedBy;

    @Column(name = "approved_at", nullable = false)
    private LocalDateTime approvedAt;

    public KnowledgeEntry() {
        this.id = UUID.randomUUID().toString();
        this.approvedAt = LocalDateTime.now();
        this.isApproved = true;
    }

    public KnowledgeEntry(String sourceCaseId, String title, String category, String problemSummary, String solutionSummary, String verificationSummary, String anonymizedLessons, String tags, String approvedBy) {
        this();
        this.sourceCaseId = sourceCaseId;
        this.title = title;
        this.category = category;
        this.problemSummary = problemSummary;
        this.solutionSummary = solutionSummary;
        this.verificationSummary = verificationSummary;
        this.anonymizedLessons = anonymizedLessons;
        this.tags = tags;
        this.approvedBy = approvedBy != null ? approvedBy : "User";
        this.isApproved = true;
    }

    // Getters and Setters
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
