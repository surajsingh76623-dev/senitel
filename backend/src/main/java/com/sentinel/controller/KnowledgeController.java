package com.sentinel.controller;

import com.sentinel.dto.KnowledgeEntryDto;
import com.sentinel.dto.PublishKnowledgeRequest;
import com.sentinel.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Knowledge Library", description = "Reusable library of explicitly approved, anonymized investigation outcomes and verified lessons")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping("/api/cases/{caseId}/publish-knowledge")
    @Operation(summary = "Explicitly approve and publish an anonymized problem resolution into the reusable knowledge library")
    public ResponseEntity<KnowledgeEntryDto> publishKnowledge(
            @PathVariable String caseId,
            @Valid @RequestBody PublishKnowledgeRequest request) {
        KnowledgeEntryDto created = knowledgeService.publishLesson(caseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/api/knowledge")
    @Operation(summary = "Publish a standalone approved knowledge entry")
    public ResponseEntity<KnowledgeEntryDto> publishStandaloneKnowledge(
            @Valid @RequestBody PublishKnowledgeRequest request) {
        KnowledgeEntryDto created = knowledgeService.publishLesson(null, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/knowledge")
    @Operation(summary = "Search and filter approved knowledge library entries")
    public ResponseEntity<List<KnowledgeEntryDto>> searchKnowledge(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category) {
        List<KnowledgeEntryDto> entries = knowledgeService.searchKnowledge(query, category);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/api/knowledge/{id}")
    @Operation(summary = "Get details of an approved knowledge entry")
    public ResponseEntity<KnowledgeEntryDto> getKnowledgeById(@PathVariable String id) {
        KnowledgeEntryDto entry = knowledgeService.getKnowledgeById(id);
        return ResponseEntity.ok(entry);
    }
}
