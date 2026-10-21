package com.sentinel.domain.enums;

public enum VerificationType {
    MANUAL("Manual Protocol / User Testing"),
    DETERMINISTIC_CALCULATOR("Deterministic Math/Business Calculator"),
    CODE_CHECK("Automated Logic & Code Assertion");

    private final String displayName;

    VerificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
