package com.sentinel.domain.enums;

public enum ProblemCategory {
    SOFTWARE_DEBUGGING("Software Debugging & Technical Troubleshooting"),
    ACADEMIC_MATH("Academic & Mathematical Problem-Solving"),
    DATA_BUSINESS_ANALYSIS("Data & Business Analysis"),
    GENERAL("General Problem & Systems Inquiry");

    private final String displayName;

    ProblemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
