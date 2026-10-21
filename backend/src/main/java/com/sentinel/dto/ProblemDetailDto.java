package com.sentinel.dto;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.ProblemUrgency;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProblemDetailDto {

    private String id;
    private String title;
    private String description;
    private ProblemCategory category;
    private String categoryDisplayName;
    private String goal;
    private String constraintsText;
    private ProblemUrgency urgency;
    private ProblemStatus status;
    private String statusDisplayName;
    private String selectedSolutionId;
    private String userNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<EvidenceItemDto> evidenceItems = new ArrayList<>();
    private InvestigationFindingDto investigationFinding;
    private List<CandidateSolutionDto> candidateSolutions = new ArrayList<>();
    private List<ActionItemDto> actionItems = new ArrayList<>();
    private List<VerificationRunDto> verificationRuns = new ArrayList<>();
    private List<AuditEventDto> auditEvents = new ArrayList<>();

    public ProblemDetailDto() {}

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

    public String getConstraintsText() { return constraintsText; }
    public void setConstraintsText(String constraintsText) { this.constraintsText = constraintsText; }

    public ProblemUrgency getUrgency() { return urgency; }
    public void setUrgency(ProblemUrgency urgency) { this.urgency = urgency; }

    public ProblemStatus getStatus() { return status; }
    public void setStatus(ProblemStatus status) {
        this.status = status;
        this.statusDisplayName = status != null ? status.getDisplayName() : null;
    }

    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }

    public String getSelectedSolutionId() { return selectedSolutionId; }
    public void setSelectedSolutionId(String selectedSolutionId) { this.selectedSolutionId = selectedSolutionId; }

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String userNotes) { this.userNotes = userNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<EvidenceItemDto> getEvidenceItems() { return evidenceItems; }
    public void setEvidenceItems(List<EvidenceItemDto> evidenceItems) { this.evidenceItems = evidenceItems; }

    public InvestigationFindingDto getInvestigationFinding() { return investigationFinding; }
    public void setInvestigationFinding(InvestigationFindingDto investigationFinding) { this.investigationFinding = investigationFinding; }

    public List<CandidateSolutionDto> getCandidateSolutions() { return candidateSolutions; }
    public void setCandidateSolutions(List<CandidateSolutionDto> candidateSolutions) { this.candidateSolutions = candidateSolutions; }

    public List<ActionItemDto> getActionItems() { return actionItems; }
    public void setActionItems(List<ActionItemDto> actionItems) { this.actionItems = actionItems; }

    public List<VerificationRunDto> getVerificationRuns() { return verificationRuns; }
    public void setVerificationRuns(List<VerificationRunDto> verificationRuns) { this.verificationRuns = verificationRuns; }

    public List<AuditEventDto> getAuditEvents() { return auditEvents; }
    public void setAuditEvents(List<AuditEventDto> auditEvents) { this.auditEvents = auditEvents; }
}
