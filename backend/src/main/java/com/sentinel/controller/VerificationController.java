package com.sentinel.controller;

import com.sentinel.dto.CreateVerificationRunRequest;
import com.sentinel.dto.DeterministicCheckRequest;
import com.sentinel.dto.DeterministicCheckResponse;
import com.sentinel.dto.VerificationRunDto;
import com.sentinel.service.VerificationService;
import com.sentinel.service.ai.VerificationPlanResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{id}/verifications")
@Tag(name = "Verification Engine", description = "Empirical test recording, reproducible deterministic checks, and verification plan generation")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping
    @Operation(summary = "Record an empirical verification test run with expected/actual results and evidence")
    public ResponseEntity<VerificationRunDto> recordVerification(
            @PathVariable String id,
            @Valid @RequestBody CreateVerificationRunRequest request) {
        VerificationRunDto created = verificationService.recordVerificationRun(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/deterministic-check")
    @Operation(summary = "Execute deterministic mathematical, performance, or economic verification calculators")
    public ResponseEntity<DeterministicCheckResponse> executeDeterministicCheck(
            @PathVariable String id,
            @Valid @RequestBody DeterministicCheckRequest request) {
        DeterministicCheckResponse response = verificationService.executeDeterministicCheck(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    @Operation(summary = "Generate a suggested verification plan for a specific solution")
    public ResponseEntity<VerificationPlanResult> getVerificationPlan(
            @PathVariable String id,
            @RequestParam String solutionId) {
        VerificationPlanResult plan = verificationService.generateVerificationPlan(id, solutionId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping
    @Operation(summary = "Get all verification test runs recorded for this problem case")
    public ResponseEntity<List<VerificationRunDto>> getRunsForCase(@PathVariable String id) {
        List<VerificationRunDto> runs = verificationService.getRunsForCase(id);
        return ResponseEntity.ok(runs);
    }
}
