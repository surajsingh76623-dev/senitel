package com.sentinel.controller;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.dto.*;
import com.sentinel.service.ProblemCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@Tag(name = "Problem Cases", description = "Problem case creation, retrieval, updates, and lifecycle transitions")
public class ProblemCaseController {

    private final ProblemCaseService problemCaseService;

    public ProblemCaseController(ProblemCaseService problemCaseService) {
        this.problemCaseService = problemCaseService;
    }

    @PostMapping
    @Operation(summary = "Create a new problem case")
    public ResponseEntity<ProblemDetailDto> createCase(@Valid @RequestBody CreateProblemRequest request) {
        ProblemDetailDto created = problemCaseService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List and search problem cases with category, status, and keyword filters")
    public ResponseEntity<List<ProblemSummaryDto>> getAllCases(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ProblemCategory category,
            @RequestParam(required = false) ProblemStatus status) {
        List<ProblemSummaryDto> cases = problemCaseService.getAllCases(query, category, status);
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full investigation workspace details for a problem case")
    public ResponseEntity<ProblemDetailDto> getCaseById(@PathVariable String id) {
        ProblemDetailDto detail = problemCaseService.getCaseById(id);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update problem case parameters and user notes")
    public ResponseEntity<ProblemDetailDto> updateCase(
            @PathVariable String id,
            @Valid @RequestBody UpdateProblemRequest request) {
        ProblemDetailDto updated = problemCaseService.updateCase(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "Transition case lifecycle status with state machine guard enforcement")
    public ResponseEntity<ProblemDetailDto> transitionStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusTransitionRequest request) {
        ProblemDetailDto updated = problemCaseService.transitionStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a problem case")
    public ResponseEntity<Void> deleteCase(@PathVariable String id) {
        problemCaseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
