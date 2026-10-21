package com.sentinel.domain.enums;

public enum VerificationStatus {
    PENDING("Pending Verification"),
    PASSED("Passed"),
    FAILED("Failed"),
    INCONCLUSIVE("Inconclusive");

    private final String displayName;

    VerificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
