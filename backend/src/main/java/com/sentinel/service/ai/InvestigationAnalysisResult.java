package com.sentinel.service.ai;

import java.util.List;

public record InvestigationAnalysisResult(
        List<String> knownFacts,
        List<String> assumptions,
        List<String> missingInformation,
        List<String> identifiedConstraints,
        List<String> successCriteria,
        List<String> clarificationQuestions,
        String diagnosticSummary,
        Double confidenceScore,
        boolean isDemoOutput
) {}
