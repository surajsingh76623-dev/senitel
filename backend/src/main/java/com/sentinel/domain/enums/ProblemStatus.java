package com.sentinel.domain.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ProblemStatus {
    DRAFT("Draft"),
    INVESTIGATING("Investigating"),
    PROPOSED("Proposed"),
    IN_PROGRESS("In Progress"),
    USER_REPORTED_SUCCESS("User-Reported Success"),
    VERIFIED("Verified"),
    FAILED("Failed"),
    NEEDS_MORE_EVIDENCE("Needs More Evidence");

    private final String displayName;

    ProblemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canTransitionTo(ProblemStatus targetStatus) {
        if (this == targetStatus) {
            return true;
        }

        return switch (this) {
            case DRAFT -> EnumSet.of(INVESTIGATING, NEEDS_MORE_EVIDENCE, FAILED).contains(targetStatus);
            case INVESTIGATING -> EnumSet.of(PROPOSED, NEEDS_MORE_EVIDENCE, FAILED).contains(targetStatus);
            case PROPOSED -> EnumSet.of(IN_PROGRESS, INVESTIGATING, NEEDS_MORE_EVIDENCE, FAILED).contains(targetStatus);
            case IN_PROGRESS -> EnumSet.of(USER_REPORTED_SUCCESS, VERIFIED, FAILED, NEEDS_MORE_EVIDENCE, PROPOSED).contains(targetStatus);
            case USER_REPORTED_SUCCESS -> EnumSet.of(VERIFIED, FAILED, IN_PROGRESS).contains(targetStatus);
            case NEEDS_MORE_EVIDENCE -> EnumSet.of(INVESTIGATING, PROPOSED, FAILED).contains(targetStatus);
            case VERIFIED -> EnumSet.of(IN_PROGRESS, FAILED).contains(targetStatus); // Re-open or invalidate
            case FAILED -> EnumSet.of(INVESTIGATING, IN_PROGRESS, DRAFT).contains(targetStatus); // Re-try
        };
    }

    public Set<ProblemStatus> getAllowedTransitions() {
        return switch (this) {
            case DRAFT -> EnumSet.of(INVESTIGATING, NEEDS_MORE_EVIDENCE, FAILED);
            case INVESTIGATING -> EnumSet.of(PROPOSED, NEEDS_MORE_EVIDENCE, FAILED);
            case PROPOSED -> EnumSet.of(IN_PROGRESS, INVESTIGATING, NEEDS_MORE_EVIDENCE, FAILED);
            case IN_PROGRESS -> EnumSet.of(USER_REPORTED_SUCCESS, VERIFIED, FAILED, NEEDS_MORE_EVIDENCE, PROPOSED);
            case USER_REPORTED_SUCCESS -> EnumSet.of(VERIFIED, FAILED, IN_PROGRESS);
            case NEEDS_MORE_EVIDENCE -> EnumSet.of(INVESTIGATING, PROPOSED, FAILED);
            case VERIFIED -> EnumSet.of(IN_PROGRESS, FAILED);
            case FAILED -> EnumSet.of(INVESTIGATING, IN_PROGRESS, DRAFT);
        };
    }
}
