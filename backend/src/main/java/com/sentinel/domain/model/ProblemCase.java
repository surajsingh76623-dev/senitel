package com.sentinel.domain.model;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.ProblemUrgency;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "problem_cases")
public class ProblemCase {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50, nullable = false)
    private ProblemCategory category;

    @Column(name = "goal", columnDefinition = "TEXT", nullable = false)
    private String goal;

    @Column(name = "constraints_text", columnDefinition = "TEXT")
    private String constraintsText;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", length = 20, nullable = false)
    private ProblemUrgency urgency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private ProblemStatus status;

    @Column(name = "selected_solution_id", length = 36)
    private String selectedSolutionId;

    @Column(name = "user_notes", columnDefinition = "TEXT")
    private String userNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EvidenceItem> evidenceItems = new ArrayList<>();

    @OneToOne(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InvestigationFinding investigationFinding;

    @OneToMany(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("solutionIndex ASC")
    private List<CandidateSolution> candidateSolutions = new ArrayList<>();

    @OneToMany(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepNumber ASC")
    private List<ActionItem> actionItems = new ArrayList<>();

    @OneToMany(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("recordedAt DESC")
    private List<VerificationRun> verificationRuns = new ArrayList<>();

    @OneToMany(mappedBy = "problemCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp DESC")
    private List<AuditEvent> auditEvents = new ArrayList<>();

    public ProblemCase() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProblemStatus.DRAFT;
        this.urgency = ProblemUrgency.MEDIUM;
    }

    public ProblemCase(String title, String description, ProblemCategory category, String goal, String constraintsText, ProblemUrgency urgency) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.goal = goal;
        this.constraintsText = constraintsText;
        this.urgency = urgency != null ? urgency : ProblemUrgency.MEDIUM;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProblemCategory getCategory() { return category; }
    public void setCategory(ProblemCategory category) { this.category = category; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getConstraintsText() { return constraintsText; }
    public void setConstraintsText(String constraintsText) { this.constraintsText = constraintsText; }

    public ProblemUrgency getUrgency() { return urgency; }
    public void setUrgency(ProblemUrgency urgency) { this.urgency = urgency; }

    public ProblemStatus getStatus() { return status; }
    public void setStatus(ProblemStatus status) { this.status = status; }

    public String getSelectedSolutionId() { return selectedSolutionId; }
    public void setSelectedSolutionId(String selectedSolutionId) { this.selectedSolutionId = selectedSolutionId; }

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String userNotes) { this.userNotes = userNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<EvidenceItem> getEvidenceItems() { return evidenceItems; }
    public void setEvidenceItems(List<EvidenceItem> evidenceItems) { this.evidenceItems = evidenceItems; }

    public InvestigationFinding getInvestigationFinding() { return investigationFinding; }
    public void setInvestigationFinding(InvestigationFinding investigationFinding) { this.investigationFinding = investigationFinding; }

    public List<CandidateSolution> getCandidateSolutions() { return candidateSolutions; }
    public void setCandidateSolutions(List<CandidateSolution> candidateSolutions) { this.candidateSolutions = candidateSolutions; }

    public List<ActionItem> getActionItems() { return actionItems; }
    public void setActionItems(List<ActionItem> actionItems) { this.actionItems = actionItems; }

    public List<VerificationRun> getVerificationRuns() { return verificationRuns; }
    public void setVerificationRuns(List<VerificationRun> verificationRuns) { this.verificationRuns = verificationRuns; }

    public List<AuditEvent> getAuditEvents() { return auditEvents; }
    public void setAuditEvents(List<AuditEvent> auditEvents) { this.auditEvents = auditEvents; }
}
