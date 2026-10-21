package com.sentinel.service.ai;

import java.util.List;

public record ClarificationEvaluationResult(
        List<String> newKnownFacts,
        List<String> updatedAssumptions,
        List<String> remainingUnknowns,
        String updatedDiagnosticSummary,
        Double updatedConfidenceScore
) {}
