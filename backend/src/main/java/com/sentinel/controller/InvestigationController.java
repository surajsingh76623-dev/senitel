package com.sentinel.controller;

import com.sentinel.dto.*;
import com.sentinel.service.InvestigationWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases/{id}")
@Tag(name = "Investigation Workflow", description = "AI investigation decomposition, clarification questions, solution selection, and action items")
public class InvestigationController {

    private final InvestigationWorkflowService workflowService;

    public InvestigationController(InvestigationWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/investigate")
    @Operation(summary = "Trigger AI problem investigation: separates facts, assumptions, missing info, constraints, and proposes solutions")
    public ResponseEntity<ProblemDetailDto> triggerInvestigation(@PathVariable String id) {
        ProblemDetailDto result = workflowService.triggerInvestigation(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/clarify")
    @Operation(summary = "Submit user answers to targeted clarification questions to refine diagnostic certainty")
    public ResponseEntity<ProblemDetailDto> submitClarification(
            @PathVariable String id,
            @RequestBody ClarificationAnswerRequest request) {
        ProblemDetailDto result = workflowService.submitClarification(id, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/select-solution")
    @Operation(summary = "Designate a candidate solution and generate the practical action plan checklist")
    public ResponseEntity<ProblemDetailDto> selectSolution(
            @PathVariable String id,
            @Valid @RequestBody SelectSolutionRequest request) {
        ProblemDetailDto result = workflowService.selectSolution(id, request);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/actions/{actionId}")
    @Operation(summary = "Toggle completion status of an action plan step")
    public ResponseEntity<ActionItemDto> toggleActionItem(
            @PathVariable String id,
            @PathVariable String actionId,
            @Valid @RequestBody UpdateActionItemRequest request) {
        ActionItemDto updated = workflowService.toggleActionItem(id, actionId, request.getIsCompleted());
        return ResponseEntity.ok(updated);
    }
}
