package com.sentinel.domain.enums;

public enum ProblemCategory {
    SOFTWARE_DEBUGGING("Software & Technical Troubleshooting"),
    ACADEMIC_MATH("Study & Academic / Mathematics"),
    ENGINEERING_TECHNICAL("Engineering (Civil, Mechanical, Electrical)"),
    DATA_BUSINESS_ANALYSIS("Business & Data Analysis"),
    GENERAL("General Problem & Everyday Inquiry");

    private final String displayName;

    ProblemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
