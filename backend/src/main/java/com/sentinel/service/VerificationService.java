package com.sentinel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.enums.VerificationType;
import com.sentinel.domain.model.CandidateSolution;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.domain.model.VerificationRun;
import com.sentinel.dto.CreateVerificationRunRequest;
import com.sentinel.dto.DeterministicCheckRequest;
import com.sentinel.dto.DeterministicCheckResponse;
import com.sentinel.dto.VerificationRunDto;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.CandidateSolutionRepository;
import com.sentinel.repository.ProblemCaseRepository;
import com.sentinel.repository.VerificationRunRepository;
import com.sentinel.service.ai.AiInvestigationService;
import com.sentinel.service.ai.VerificationPlanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationService {

    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    private final ProblemCaseRepository problemCaseRepository;
    private final CandidateSolutionRepository candidateSolutionRepository;
    private final VerificationRunRepository verificationRunRepository;
    private final DeterministicVerificationEngine deterministicVerificationEngine;
    private final AiInvestigationService aiInvestigationService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public VerificationService(
            ProblemCaseRepository problemCaseRepository,
            CandidateSolutionRepository candidateSolutionRepository,
            VerificationRunRepository verificationRunRepository,
            DeterministicVerificationEngine deterministicVerificationEngine,
            AiInvestigationService aiInvestigationService,
            AuditService auditService,
            ObjectMapper objectMapper) {
        this.problemCaseRepository = problemCaseRepository;
        this.candidateSolutionRepository = candidateSolutionRepository;
        this.verificationRunRepository = verificationRunRepository;
        this.deterministicVerificationEngine = deterministicVerificationEngine;
        this.aiInvestigationService = aiInvestigationService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public VerificationRunDto recordVerificationRun(String caseId, CreateVerificationRunRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        CandidateSolution solution = null;
        if (request.getSolutionId() != null && !request.getSolutionId().isBlank()) {
            solution = candidateSolutionRepository.findById(request.getSolutionId()).orElse(null);
        }

        VerificationRun run = new VerificationRun(
                problemCase,
                solution,
                request.getTestProcedure(),
                request.getExpectedResult(),
                request.getVerificationType(),
                request.getRecordedBy()
        );
        run.setActualResult(request.getActualResult());
        run.setStatus(request.getStatus());
        run.setEvidenceNotes(request.getEvidenceNotes());
        run.setIsReproducible(request.getIsReproducible() != null ? request.getIsReproducible() : true);
        run.setRecordedAt(LocalDateTime.now());

        VerificationRun saved = verificationRunRepository.save(run);

        // Update problem case status based on verification outcome
        if (saved.getStatus() == VerificationStatus.PASSED) {
            problemCase.setStatus(ProblemStatus.VERIFIED);
            problemCase.setUpdatedAt(LocalDateTime.now());
            problemCaseRepository.save(problemCase);
        } else if (saved.getStatus() == VerificationStatus.FAILED) {
            problemCase.setStatus(ProblemStatus.FAILED);
            problemCase.setUpdatedAt(LocalDateTime.now());
            problemCaseRepository.save(problemCase);
        } else if (saved.getStatus() == VerificationStatus.INCONCLUSIVE) {
            problemCase.setStatus(ProblemStatus.NEEDS_MORE_EVIDENCE);
            problemCase.setUpdatedAt(LocalDateTime.now());
            problemCaseRepository.save(problemCase);
        }

        auditService.logEvent(
                problemCase,
                AuditEventType.VERIFICATION_EXECUTED,
                request.getRecordedBy() != null ? request.getRecordedBy() : "User",
                String.format("Recorded verification run [%s]: %s (Status: %s)",
                        saved.getVerificationType(), saved.getTestProcedure(), saved.getStatus()),
                "{\"runId\":\"" + saved.getId() + "\",\"status\":\"" + saved.getStatus() + "\",\"type\":\"" + saved.getVerificationType() + "\"}"
        );

        return toDto(saved);
    }

    @Transactional
    public DeterministicCheckResponse executeDeterministicCheck(String caseId, DeterministicCheckRequest request) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        CandidateSolution solution = null;
        if (request.getSolutionId() != null && !request.getSolutionId().isBlank()) {
            solution = candidateSolutionRepository.findById(request.getSolutionId()).orElse(null);
        } else if (problemCase.getSelectedSolutionId() != null) {
            solution = candidateSolutionRepository.findById(problemCase.getSelectedSolutionId()).orElse(null);
        }

        // Execute deterministic check
        DeterministicCheckResponse response = deterministicVerificationEngine.executeCheck(request);

        // Persist as a formal VerificationRun
        VerificationRun run = new VerificationRun(
                problemCase,
                solution,
                response.getTestProcedure(),
                response.getExpectedCondition(),
                VerificationType.DETERMINISTIC_CALCULATOR,
                "Deterministic Sentinel Verification Engine"
        );
        run.setActualResult(response.getCalculationDetails());
        run.setStatus(response.getStatus());
        run.setEvidenceNotes(response.getEvidenceNotes());
        run.setIsReproducible(true);
        run.setRecordedAt(LocalDateTime.now());
        verificationRunRepository.save(run);

        // Update problem case status if test passed
        if (response.isPassed()) {
            problemCase.setStatus(ProblemStatus.VERIFIED);
            problemCase.setUpdatedAt(LocalDateTime.now());
            problemCaseRepository.save(problemCase);
        }

        try {
            String metricsJson = objectMapper.writeValueAsString(response.getOutputMetrics());
            auditService.logEvent(
                    problemCase,
                    AuditEventType.VERIFICATION_EXECUTED,
                    "Deterministic Engine",
                    String.format("Executed %s: %s (Outcome: %s)", response.getCheckName(), response.getCalculationDetails(), response.getStatus()),
                    metricsJson
            );
        } catch (Exception ignored) {}

        return response;
    }

    @Transactional(readOnly = true)
    public VerificationPlanResult generateVerificationPlan(String caseId, String solutionId) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with id: " + caseId));

        CandidateSolution solution = candidateSolutionRepository.findById(solutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Solution not found with id: " + solutionId));

        return aiInvestigationService.generateVerificationPlan(problemCase, solution);
    }

    @Transactional(readOnly = true)
    public List<VerificationRunDto> getRunsForCase(String caseId) {
        return verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc(caseId).stream()
                .map(this::toDto)
                .toList();
    }

    private VerificationRunDto toDto(VerificationRun r) {
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
}
