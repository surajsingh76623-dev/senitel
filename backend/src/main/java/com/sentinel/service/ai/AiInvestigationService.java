package com.sentinel.service.ai;

import com.sentinel.domain.model.ActionItem;
import com.sentinel.domain.model.CandidateSolution;
import com.sentinel.domain.model.EvidenceItem;
import com.sentinel.domain.model.InvestigationFinding;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.ClarificationAnswerRequest;

import java.util.List;

public interface AiInvestigationService {

    InvestigationAnalysisResult analyzeProblem(ProblemCase problem, List<EvidenceItem> evidence);

    List<CandidateSolution> generateSolutions(ProblemCase problem, InvestigationFinding finding, List<EvidenceItem> evidence);

    List<ActionItem> generateActionPlan(ProblemCase problem, CandidateSolution solution);

    VerificationPlanResult generateVerificationPlan(ProblemCase problem, CandidateSolution solution);

    ClarificationEvaluationResult evaluateClarification(ProblemCase problem, InvestigationFinding currentFinding, List<ClarificationAnswerRequest.ClarificationEntry> answers);

    String getProviderName();

    boolean isDemoMode();
}
