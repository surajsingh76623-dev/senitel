package com.sentinel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.model.*;
import com.sentinel.dto.*;
import com.sentinel.exception.InvalidStateTransitionException;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProblemCaseService {

    private static final Logger log = LoggerFactory.getLogger(ProblemCaseService.class);

    private final ProblemCaseRepository problemCaseRepository;
    private final EvidenceItemRepository evidenceItemRepository;
    private final InvestigationFindingRepository investigationFindingRepository;
    private final CandidateSolutionRepository candidateSolutionRepository;
    private final ActionItemRepository actionItemRepository;
    private final VerificationRunRepository verificationRunRepository;
    private final KnowledgeEntryRepository knowledgeEntryRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public ProblemCaseService(
            ProblemCaseRepository problemCaseRepository,
            EvidenceItemRepository evidenceItemRepository,
            InvestigationFindingRepository investigationFindingRepository,
            CandidateSolutionRepository candidateSolutionRepository,
            ActionItemRepository actionItemRepository,
            VerificationRunRepository verificationRunRepository,
            KnowledgeEntryRepository knowledgeEntryRepository,
            AuditService auditService,
            ObjectMapper objectMapper) {
        this.problemCaseRepository = problemCaseRepository;
        this.evidenceItemRepository = evidenceItemRepository;
        this.investigationFindingRepository = investigationFindingRepository;
        this.candidateSolutionRepository = candidateSolutionRepository;
        this.actionItemRepository = actionItemRepository;
        this.verificationRunRepository = verificationRunRepository;
        this.knowledgeEntryRepository = knowledgeEntryRepository;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProblemDetailDto createCase(CreateProblemRequest request) {
        ProblemCase problemCase = new ProblemCase(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getGoal(),
                request.getConstraintsText(),
                request.getUrgency()
        );
        problemCase.setStatus(ProblemStatus.DRAFT);
        ProblemCase saved = problemCaseRepository.save(problemCase);

        auditService.logEvent(
                saved,
                AuditEventType.CASE_CREATED,
                "User",
                "Created new problem investigation case: " + saved.getTitle(),
                "{\"category\":\"" + saved.getCategory() + "\",\"urgency\":\"" + saved.getUrgency() + "\"}"
        );

        return getCaseById(saved.getId());
    }

    @Transactional(readOnly = true)
    public ProblemDetailDto getCaseById(String id) {
        ProblemCase problemCase = problemCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem case not found with id: " + id));

        return toDetailDto(problemCase);
    }

    @Transactional(readOnly = true)
    public List<ProblemSummaryDto> getAllCases(String query, ProblemCategory category, ProblemStatus status) {
        List<ProblemCase> cases;
        if ((query != null && !query.isBlank()) || category != null || status != null) {
            cases = problemCaseRepository.searchCases(
                    (query != null && !query.isBlank()) ? query.trim() : null,
                    category,
                    status
            );
        } else {
            cases = problemCaseRepository.findAllByOrderByCreatedAtDesc();
        }

        return cases.stream().map(this::toSummaryDto).toList();
    }

    @Transactional
    public ProblemDetailDto updateCase(String id, UpdateProblemRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem case not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            problemCase.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            problemCase.setDescription(request.getDescription().trim());
        }
        if (request.getCategory() != null) {
            problemCase.setCategory(request.getCategory());
        }
        if (request.getGoal() != null && !request.getGoal().isBlank()) {
            problemCase.setGoal(request.getGoal().trim());
        }
        if (request.getConstraintsText() != null) {
            problemCase.setConstraintsText(request.getConstraintsText().trim());
        }
        if (request.getUrgency() != null) {
            problemCase.setUrgency(request.getUrgency());
        }
        if (request.getUserNotes() != null) {
            problemCase.setUserNotes(request.getUserNotes().trim());
        }

        problemCase.setUpdatedAt(LocalDateTime.now());
        ProblemCase saved = problemCaseRepository.save(problemCase);

        auditService.logEvent(
                saved,
                AuditEventType.CASE_UPDATED,
                "User",
                "Updated case parameters and notes",
                null
        );

        return getCaseById(saved.getId());
    }

    @Transactional
    public ProblemDetailDto transitionStatus(String id, StatusTransitionRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem case not found with id: " + id));

        ProblemStatus currentStatus = problemCase.getStatus();
        ProblemStatus targetStatus = request.getTargetStatus();

        if (currentStatus == targetStatus) {
            return getCaseById(id);
        }

        // Validate state machine rules
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new InvalidStateTransitionException(String.format(
                    "Invalid status transition from '%s' to '%s'. Allowed transitions from %s are: %s",
                    currentStatus.getDisplayName(),
                    targetStatus.getDisplayName(),
                    currentStatus.getDisplayName(),
                    currentStatus.getAllowedTransitions()
            ));
        }

        // Verification guard rule: Never mark a solution Verified merely because model says it worked
        if (targetStatus == ProblemStatus.VERIFIED) {
            List<VerificationRun> runs = verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc(id);
            boolean hasPassingRun = runs.stream().anyMatch(r -> r.getStatus() == VerificationStatus.PASSED);

            if (!hasPassingRun) {
                throw new InvalidStateTransitionException(
                        "Cannot transition case to 'VERIFIED': No passing verification run exists. " +
                        "A solution must have an independently recorded passing test run before it can be marked Verified. " +
                        "If you believe the fix worked without test verification, use status 'USER_REPORTED_SUCCESS'."
                );
            }
        }

        // In Progress guard rule: must have a candidate solution selected
        if (targetStatus == ProblemStatus.IN_PROGRESS && problemCase.getSelectedSolutionId() == null) {
            throw new InvalidStateTransitionException(
                    "Cannot transition case to 'IN_PROGRESS': No candidate solution has been selected yet. " +
                    "Please select a solution to generate the action plan checklist."
            );
        }

        problemCase.setStatus(targetStatus);
        problemCase.setUpdatedAt(LocalDateTime.now());
        problemCaseRepository.save(problemCase);

        auditService.logEvent(
                problemCase,
                AuditEventType.STATUS_CHANGED,
                request.getActor() != null ? request.getActor() : "User",
                String.format("Transitioned status from %s to %s%s",
                        currentStatus.getDisplayName(),
                        targetStatus.getDisplayName(),
                        request.getReason() != null ? " (Reason: " + request.getReason() + ")" : ""),
                "{\"oldStatus\":\"" + currentStatus + "\",\"newStatus\":\"" + targetStatus + "\"}"
        );

        return getCaseById(id);
    }

    @Transactional
    public void deleteCase(String id) {
        ProblemCase problemCase = problemCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem case not found with id: " + id));
        problemCaseRepository.delete(problemCase);
    }

    @Transactional(readOnly = true)
    public SystemStatsDto getSystemStats() {
        SystemStatsDto stats = new SystemStatsDto();
        stats.setTotalCases(problemCaseRepository.count());
        stats.setDraftCases(problemCaseRepository.countByStatus(ProblemStatus.DRAFT));
        stats.setInvestigatingCases(problemCaseRepository.countByStatus(ProblemStatus.INVESTIGATING));
        stats.setProposedCases(problemCaseRepository.countByStatus(ProblemStatus.PROPOSED));
        stats.setInProgressCases(problemCaseRepository.countByStatus(ProblemStatus.IN_PROGRESS));
        stats.setUserSuccessCases(problemCaseRepository.countByStatus(ProblemStatus.USER_REPORTED_SUCCESS));
        stats.setVerifiedCases(problemCaseRepository.countByStatus(ProblemStatus.VERIFIED));
        stats.setFailedCases(problemCaseRepository.countByStatus(ProblemStatus.FAILED));
        stats.setNeedsEvidenceCases(problemCaseRepository.countByStatus(ProblemStatus.NEEDS_MORE_EVIDENCE));
        stats.setKnowledgeEntriesCount(knowledgeEntryRepository.count());
        stats.setEvidenceFilesCount(evidenceItemRepository.count());

        Map<String, Long> byCategory = new HashMap<>();
        for (ProblemCategory cat : ProblemCategory.values()) {
            byCategory.put(cat.name(), (long) problemCaseRepository.findByCategoryOrderByCreatedAtDesc(cat).size());
        }
        stats.setCasesByCategory(byCategory);

        return stats;
    }

    public ProblemSummaryDto toSummaryDto(ProblemCase p) {
        ProblemSummaryDto dto = new ProblemSummaryDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setGoal(p.getGoal());
        dto.setUrgency(p.getUrgency());
        dto.setStatus(p.getStatus());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

        List<VerificationRun> runs = verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc(p.getId());
        dto.setVerificationRunsCount(runs.size());
        dto.setHasVerifiedRun(runs.stream().anyMatch(r -> r.getStatus() == VerificationStatus.PASSED));

        List<ActionItem> actions = actionItemRepository.findByProblemCaseIdOrderByStepNumberAsc(p.getId());
        dto.setActionItemsCount(actions.size());
        dto.setCompletedActionItemsCount((int) actions.stream().filter(ActionItem::getIsCompleted).count());

        dto.setEvidenceCount(evidenceItemRepository.findByProblemCaseIdOrderByUploadedAtAsc(p.getId()).size());
        dto.setSolutionsCount(candidateSolutionRepository.findByProblemCaseIdOrderBySolutionIndexAsc(p.getId()).size());

        return dto;
    }

    public ProblemDetailDto toDetailDto(ProblemCase p) {
        ProblemDetailDto dto = new ProblemDetailDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setCategory(p.getCategory());
        dto.setGoal(p.getGoal());
        dto.setConstraintsText(p.getConstraintsText());
        dto.setUrgency(p.getUrgency());
        dto.setStatus(p.getStatus());
        dto.setSelectedSolutionId(p.getSelectedSolutionId());
        dto.setUserNotes(p.getUserNotes());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());

        // Evidence
        dto.setEvidenceItems(evidenceItemRepository.findByProblemCaseIdOrderByUploadedAtAsc(p.getId()).stream()
                .map(e -> new EvidenceItemDto(
                        e.getId(), p.getId(), e.getOriginalFilename(), e.getMimeType(),
                        e.getFileSize(), e.getChecksumSha256(), e.getFileSummary(), e.getUploadedAt()))
                .toList());

        // Finding
        investigationFindingRepository.findByProblemCaseId(p.getId())
                .ifPresent(f -> dto.setInvestigationFinding(toFindingDto(f)));

        // Solutions
        dto.setCandidateSolutions(candidateSolutionRepository.findByProblemCaseIdOrderBySolutionIndexAsc(p.getId()).stream()
                .map(this::toSolutionDto)
                .toList());

        // Actions
        dto.setActionItems(actionItemRepository.findByProblemCaseIdOrderByStepNumberAsc(p.getId()).stream()
                .map(a -> new ActionItemDto(
                        a.getId(), p.getId(),
                        a.getCandidateSolution() != null ? a.getCandidateSolution().getId() : null,
                        a.getStepNumber(), a.getTitle(), a.getDescription(), a.getIsCompleted(),
                        a.getCompletedAt(), a.getCreatedAt()))
                .toList());

        // Verifications
        dto.setVerificationRuns(verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc(p.getId()).stream()
                .map(this::toVerificationRunDto)
                .toList());

        // Audit Events
        dto.setAuditEvents(auditService.getTimelineForCase(p.getId()));

        return dto;
    }

    private InvestigationFindingDto toFindingDto(InvestigationFinding f) {
        InvestigationFindingDto dto = new InvestigationFindingDto();
        dto.setId(f.getId());
        dto.setCaseId(f.getProblemCase() != null ? f.getProblemCase().getId() : null);
        dto.setKnownFacts(parseJsonList(f.getKnownFacts()));
        dto.setAssumptions(parseJsonList(f.getAssumptions()));
        dto.setMissingInformation(parseJsonList(f.getMissingInformation()));
        dto.setIdentifiedConstraints(parseJsonList(f.getIdentifiedConstraints()));
        dto.setSuccessCriteria(parseJsonList(f.getSuccessCriteria()));
        dto.setClarificationQuestions(parseJsonList(f.getClarificationQuestions()));
        dto.setClarificationResponses(parseClarificationResponses(f.getClarificationResponses()));
        dto.setDiagnosticSummary(f.getDiagnosticSummary());
        dto.setConfidenceScore(f.getConfidenceScore());
        dto.setIsDemoOutput(f.getIsDemoOutput());
        dto.setAnalyzedAt(f.getAnalyzedAt());
        return dto;
    }

    private CandidateSolutionDto toSolutionDto(CandidateSolution s) {
        CandidateSolutionDto dto = new CandidateSolutionDto();
        dto.setId(s.getId());
        dto.setCaseId(s.getProblemCase() != null ? s.getProblemCase().getId() : null);
        dto.setSolutionIndex(s.getSolutionIndex());
        dto.setTitle(s.getTitle());
        dto.setSummary(s.getSummary());
        dto.setReasoning(s.getReasoning());
        dto.setSupportingEvidence(parseJsonList(s.getSupportingEvidence()));
        dto.setBenefits(parseJsonList(s.getBenefits()));
        dto.setTradeoffs(parseJsonList(s.getTradeoffs()));
        dto.setRisks(parseJsonList(s.getRisks()));
        dto.setEffortLevel(s.getEffortLevel());
        dto.setConfidenceLimitations(s.getConfidenceLimitations());
        dto.setIsSelected(s.getIsSelected());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }

    private VerificationRunDto toVerificationRunDto(VerificationRun r) {
        VerificationRunDto dto = new VerificationRunDto();
        dto.setId(r.getId());
        dto.setCaseId(r.getProblemCase() != null ? r.getProblemCase().getId() : null);
        dto.setSolutionId(r.getCandidateSolution() != null ? r.getCandidateSolution().getId() : null);
        dto.setTestProcedure(r.getTestProcedure());
        dto.setExpectedResult(r.getExpectedResult());
        dto.setActualResult(r.getActualResult());
        dto.setStatus(r.getStatus());
        dto.setVerificationType(r.getVerificationType());
        dto.setEvidenceNotes(r.getEvidenceNotes());
        dto.setRecordedBy(r.getRecordedBy());
        dto.setRecordedAt(r.getRecordedAt());
        dto.setIsReproducible(r.getIsReproducible());
        return dto;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of(json);
        }
    }

    private List<ClarificationAnswerRequest.ClarificationEntry> parseClarificationResponses(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<ClarificationAnswerRequest.ClarificationEntry>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
