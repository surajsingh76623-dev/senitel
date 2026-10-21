package com.sentinel.domain.enums;

public enum AuditEventType {
    CASE_CREATED("Problem Case Created"),
    CASE_UPDATED("Problem Case Updated"),
    EVIDENCE_UPLOADED("Evidence Attached"),
    INVESTIGATION_PERFORMED("Investigation Analyzed"),
    CLARIFICATION_SUBMITTED("Clarification Answered"),
    SOLUTION_PROPOSED("Solutions Generated"),
    SOLUTION_SELECTED("Candidate Solution Selected"),
    ACTION_ITEM_UPDATED("Action Item Status Updated"),
    VERIFICATION_EXECUTED("Verification Run Executed"),
    STATUS_CHANGED("Case Lifecycle Status Changed"),
    KNOWLEDGE_PUBLISHED("Case Published to Reusable Knowledge Library");

    private final String description;

    AuditEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
