package com.sentinel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.model.ActionItem;
import com.sentinel.domain.model.CandidateSolution;
import com.sentinel.domain.model.EvidenceItem;
import com.sentinel.domain.model.InvestigationFinding;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.ActionItemDto;
import com.sentinel.dto.ClarificationAnswerRequest;
import com.sentinel.dto.ProblemDetailDto;
import com.sentinel.dto.SelectSolutionRequest;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.ActionItemRepository;
import com.sentinel.repository.CandidateSolutionRepository;
import com.sentinel.repository.EvidenceItemRepository;
import com.sentinel.repository.InvestigationFindingRepository;
import com.sentinel.repository.ProblemCaseRepository;
import com.sentinel.service.ai.AiInvestigationService;
import com.sentinel.service.ai.ClarificationEvaluationResult;
import com.sentinel.service.ai.InvestigationAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvestigationWorkflowService {

    private static final Logger log = LoggerFactory.getLogger(InvestigationWorkflowService.class);

    private final ProblemCaseRepository problemCaseRepository;
    private final EvidenceItemRepository evidenceItemRepository;
    private final InvestigationFindingRepository investigationFindingRepository;
    private final CandidateSolutionRepository candidateSolutionRepository;
    private final ActionItemRepository actionItemRepository;
    private final AiInvestigationService aiInvestigationService;
    private final ProblemCaseService problemCaseService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public InvestigationWorkflowService(
            ProblemCaseRepository problemCaseRepository,
            EvidenceItemRepository evidenceItemRepository,
            InvestigationFindingRepository investigationFindingRepository,
            CandidateSolutionRepository candidateSolutionRepository,
            ActionItemRepository actionItemRepository,
            AiInvestigationService aiInvestigationService,
            ProblemCaseService problemCaseService,
            AuditService auditService,
            ObjectMapper objectMapper) {
        this.problemCaseRepository = problemCaseRepository;
        this.evidenceItemRepository = evidenceItemRepository;
        this.investigationFindingRepository = investigationFindingRepository;
        this.candidateSolutionRepository = candidateSolutionRepository;
        this.actionItemRepository = actionItemRepository;
        this.aiInvestigationService = aiInvestigationService;
        this.problemCaseService = problemCaseService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProblemDetailDto triggerInvestigation(String caseId) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        log.info("Triggering investigation analysis for case: {}", caseId);

        List<EvidenceItem> evidence = evidenceItemRepository.findByProblemCaseIdOrderByUploadedAtAsc(caseId);

        // 1. Analyze submitted information into structured facts, assumptions, unknowns, constraints, criteria
        InvestigationAnalysisResult analysis = aiInvestigationService.analyzeProblem(problemCase, evidence);

        InvestigationFinding finding = investigationFindingRepository.findByProblemCaseId(caseId)
                .orElse(new InvestigationFinding(problemCase));

        finding.setKnownFacts(toJson(analysis.knownFacts()));
        finding.setAssumptions(toJson(analysis.assumptions()));
        finding.setMissingInformation(toJson(analysis.missingInformation()));
        finding.setIdentifiedConstraints(toJson(analysis.identifiedConstraints()));
        finding.setSuccessCriteria(toJson(analysis.successCriteria()));
        finding.setClarificationQuestions(toJson(analysis.clarificationQuestions()));
        finding.setDiagnosticSummary(analysis.diagnosticSummary());
        finding.setConfidenceScore(analysis.confidenceScore());
        finding.setIsDemoOutput(analysis.isDemoOutput());
        finding.setAnalyzedAt(LocalDateTime.now());
        investigationFindingRepository.save(finding);

        // 2. Propose candidate solutions
        List<CandidateSolution> solutions = aiInvestigationService.generateSolutions(problemCase, finding, evidence);
        candidateSolutionRepository.deleteByProblemCaseId(caseId);
        candidateSolutionRepository.saveAll(solutions);

        // 3. Update case status to PROPOSED
        problemCase.setStatus(ProblemStatus.PROPOSED);
        problemCase.setUpdatedAt(LocalDateTime.now());
        problemCaseRepository.save(problemCase);

        auditService.logEvent(
                problemCase,
                AuditEventType.INVESTIGATION_PERFORMED,
                aiInvestigationService.getProviderName(),
                "Completed problem investigation analysis and diagnosed root cause: " + analysis.diagnosticSummary(),
                "{\"confidenceScore\":" + analysis.confidenceScore() + ",\"isDemo\":" + analysis.isDemoOutput() + "}"
        );

        auditService.logEvent(
                problemCase,
                AuditEventType.SOLUTION_PROPOSED,
                aiInvestigationService.getProviderName(),
                "Proposed " + solutions.size() + " candidate solutions with comparative trade-off and risk profiles",
                "{\"solutionsCount\":" + solutions.size() + "}"
        );

        return problemCaseService.getCaseById(caseId);
    }

    @Transactional
    public ProblemDetailDto submitClarification(String caseId, ClarificationAnswerRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        InvestigationFinding finding = investigationFindingRepository.findByProblemCaseId(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Investigation finding not found for case: " + caseId));

        List<ClarificationAnswerRequest.ClarificationEntry> answers = request.getResponses() != null ? request.getResponses() : List.of();

        ClarificationEvaluationResult eval = aiInvestigationService.evaluateClarification(problemCase, finding, answers);

        // Merge confirmed facts
        List<String> currentFacts = fromJsonList(finding.getKnownFacts());
        currentFacts.addAll(eval.newKnownFacts());
        finding.setKnownFacts(toJson(currentFacts));

        finding.setAssumptions(toJson(eval.updatedAssumptions()));
        finding.setMissingInformation(toJson(eval.remainingUnknowns()));
        finding.setDiagnosticSummary(eval.updatedDiagnosticSummary());
        finding.setConfidenceScore(eval.updatedConfidenceScore());
        finding.setClarificationResponses(toJson(answers));
        investigationFindingRepository.save(finding);

        auditService.logEvent(
                problemCase,
                AuditEventType.CLARIFICATION_SUBMITTED,
                "User",
                "Answered " + answers.size() + " clarification questions. Confidence updated to " + eval.updatedConfidenceScore(),
                "{\"answersCount\":" + answers.size() + ",\"newConfidence\":" + eval.updatedConfidenceScore() + "}"
        );

        return problemCaseService.getCaseById(caseId);
    }

    @Transactional
    public ProblemDetailDto selectSolution(String caseId, SelectSolutionRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        CandidateSolution selectedSolution = candidateSolutionRepository.findById(request.getSolutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Solution not found with id: " + request.getSolutionId()));

        if (!selectedSolution.getProblemCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Selected solution does not belong to case: " + caseId);
        }

        // Mark selection
        List<CandidateSolution> allSolutions = candidateSolutionRepository.findByProblemCaseIdOrderBySolutionIndexAsc(caseId);
        for (CandidateSolution sol : allSolutions) {
            sol.setIsSelected(sol.getId().equals(selectedSolution.getId()));
        }
        candidateSolutionRepository.saveAll(allSolutions);

        problemCase.setSelectedSolutionId(selectedSolution.getId());

        // Generate action plan checklist
        actionItemRepository.deleteByProblemCaseId(caseId);
        List<ActionItem> actionItems = aiInvestigationService.generateActionPlan(problemCase, selectedSolution);
        actionItemRepository.saveAll(actionItems);

        // Transition status to IN_PROGRESS
        problemCase.setStatus(ProblemStatus.IN_PROGRESS);
        problemCase.setUpdatedAt(LocalDateTime.now());
        problemCaseRepository.save(problemCase);

        auditService.logEvent(
                problemCase,
                AuditEventType.SOLUTION_SELECTED,
                "User",
                "Selected Candidate Solution #" + selectedSolution.getSolutionIndex() + ": " + selectedSolution.getTitle() + ". Action plan generated with " + actionItems.size() + " steps.",
                "{\"solutionId\":\"" + selectedSolution.getId() + "\",\"actionStepsCount\":" + actionItems.size() + "}"
        );

        return problemCaseService.getCaseById(caseId);
    }

    @Transactional
    public ActionItemDto toggleActionItem(String caseId, String actionItemId, boolean completed) {
        ActionItem item = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Action item not found with id: " + actionItemId));

        if (!item.getProblemCase().getId().equals(caseId)) {
            throw new IllegalArgumentException("Action item does not belong to case: " + caseId);
        }

        item.setIsCompleted(completed);
        ActionItem saved = actionItemRepository.save(item);

        auditService.logEvent(
                item.getProblemCase(),
                AuditEventType.ACTION_ITEM_UPDATED,
                "User",
                (completed ? "Completed" : "Re-opened") + " action step #" + item.getStepNumber() + ": " + item.getTitle(),
                "{\"stepNumber\":" + item.getStepNumber() + ",\"isCompleted\":" + completed + "}"
        );

        return new ActionItemDto(
                saved.getId(),
                caseId,
                saved.getCandidateSolution() != null ? saved.getCandidateSolution().getId() : null,
                saved.getStepNumber(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getIsCompleted(),
                saved.getCompletedAt(),
                saved.getCreatedAt()
        );
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
