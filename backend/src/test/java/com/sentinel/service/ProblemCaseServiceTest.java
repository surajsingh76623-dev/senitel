package com.sentinel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.enums.ProblemUrgency;
import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.domain.model.VerificationRun;
import com.sentinel.dto.CreateProblemRequest;
import com.sentinel.dto.ProblemDetailDto;
import com.sentinel.dto.StatusTransitionRequest;
import com.sentinel.exception.InvalidStateTransitionException;
import com.sentinel.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemCaseServiceTest {

    @Mock private ProblemCaseRepository problemCaseRepository;
    @Mock private EvidenceItemRepository evidenceItemRepository;
    @Mock private InvestigationFindingRepository investigationFindingRepository;
    @Mock private CandidateSolutionRepository candidateSolutionRepository;
    @Mock private ActionItemRepository actionItemRepository;
    @Mock private VerificationRunRepository verificationRunRepository;
    @Mock private KnowledgeEntryRepository knowledgeEntryRepository;
    @Mock private AuditEventRepository auditEventRepository;

    private ProblemCaseService service;
    private AuditService auditService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        auditService = new AuditService(auditEventRepository);
        service = new ProblemCaseService(
                problemCaseRepository,
                evidenceItemRepository,
                investigationFindingRepository,
                candidateSolutionRepository,
                actionItemRepository,
                verificationRunRepository,
                knowledgeEntryRepository,
                auditService,
                objectMapper
        );
    }

    @Test
    @DisplayName("Create Problem Case: Sets initial status to DRAFT and saves")
    void createCase_setsDraftStatus() {
        CreateProblemRequest request = new CreateProblemRequest(
                "Test Bug",
                "Description of test bug",
                ProblemCategory.SOFTWARE_DEBUGGING,
                "Resolve bug",
                "None",
                ProblemUrgency.HIGH
        );

        when(problemCaseRepository.save(any(ProblemCase.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(problemCaseRepository.findById(anyString())).thenAnswer(invocation -> {
            ProblemCase p = new ProblemCase("Test Bug", "Desc", ProblemCategory.SOFTWARE_DEBUGGING, "Goal", "Constraints", ProblemUrgency.HIGH);
            p.setId(invocation.getArgument(0));
            return Optional.of(p);
        });

        ProblemDetailDto result = service.createCase(request);

        assertNotNull(result);
        assertEquals(ProblemStatus.DRAFT, result.getStatus());
        verify(problemCaseRepository).save(any(ProblemCase.class));
        verify(auditEventRepository).save(any());
    }

    @Test
    @DisplayName("Transition Status: Throws InvalidStateTransitionException on illegal jump")
    void transitionStatus_throwsOnIllegalTransition() {
        ProblemCase problemCase = new ProblemCase("Test", "Desc", ProblemCategory.SOFTWARE_DEBUGGING, "Goal", null, ProblemUrgency.MEDIUM);
        problemCase.setStatus(ProblemStatus.DRAFT);

        when(problemCaseRepository.findById("case-1")).thenReturn(Optional.of(problemCase));

        StatusTransitionRequest request = new StatusTransitionRequest(ProblemStatus.VERIFIED, "Premature attempt", "User");

        InvalidStateTransitionException ex = assertThrows(InvalidStateTransitionException.class, () ->
                service.transitionStatus("case-1", request)
        );

        assertTrue(ex.getMessage().contains("Invalid status transition"));
    }

    @Test
    @DisplayName("Verification Guard: Rejects VERIFIED status transition if no passing verification run exists")
    void transitionStatus_rejectsVerifiedWithoutPassingRun() {
        ProblemCase problemCase = new ProblemCase("Test", "Desc", ProblemCategory.SOFTWARE_DEBUGGING, "Goal", null, ProblemUrgency.MEDIUM);
        problemCase.setStatus(ProblemStatus.IN_PROGRESS);
        problemCase.setSelectedSolutionId("sol-1");

        when(problemCaseRepository.findById("case-1")).thenReturn(Optional.of(problemCase));
        // No passing verification runs
        when(verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc("case-1")).thenReturn(Collections.emptyList());

        StatusTransitionRequest request = new StatusTransitionRequest(ProblemStatus.VERIFIED, "Attempt to verify without test", "User");

        InvalidStateTransitionException ex = assertThrows(InvalidStateTransitionException.class, () ->
                service.transitionStatus("case-1", request)
        );

        assertTrue(ex.getMessage().contains("Cannot transition case to 'VERIFIED': No passing verification run exists"));
    }

    @Test
    @DisplayName("Verification Guard: Allows transition to VERIFIED when passing run exists")
    void transitionStatus_allowsVerifiedWhenPassingRunExists() {
        ProblemCase problemCase = new ProblemCase("Test", "Desc", ProblemCategory.SOFTWARE_DEBUGGING, "Goal", null, ProblemUrgency.MEDIUM);
        problemCase.setStatus(ProblemStatus.IN_PROGRESS);
        problemCase.setSelectedSolutionId("sol-1");

        VerificationRun passingRun = new VerificationRun();
        passingRun.setStatus(VerificationStatus.PASSED);

        when(problemCaseRepository.findById("case-1")).thenReturn(Optional.of(problemCase));
        when(verificationRunRepository.findByProblemCaseIdOrderByRecordedAtDesc("case-1")).thenReturn(List.of(passingRun));
        when(problemCaseRepository.save(any(ProblemCase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatusTransitionRequest request = new StatusTransitionRequest(ProblemStatus.VERIFIED, "Passed synthetic test", "User");
        ProblemDetailDto result = service.transitionStatus("case-1", request);

        assertEquals(ProblemStatus.VERIFIED, result.getStatus());
        verify(problemCaseRepository).save(problemCase);
    }
}
