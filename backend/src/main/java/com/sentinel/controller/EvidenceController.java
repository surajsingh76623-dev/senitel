package com.sentinel.controller;

import com.sentinel.dto.EvidenceItemDto;
import com.sentinel.service.EvidenceStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Evidence Files", description = "Controlled upload, metadata tracking, and secure download of evidence files")
public class EvidenceController {

    private final EvidenceStorageService evidenceStorageService;

    public EvidenceController(EvidenceStorageService evidenceStorageService) {
        this.evidenceStorageService = evidenceStorageService;
    }

    @PostMapping(value = "/api/cases/{caseId}/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload supporting evidence file (logs, screenshots, datasets, metrics)")
    public ResponseEntity<EvidenceItemDto> uploadEvidence(
            @PathVariable String caseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "summary", required = false) String summary) {
        EvidenceItemDto stored = evidenceStorageService.storeEvidence(caseId, file, summary);
        return ResponseEntity.status(HttpStatus.CREATED).body(stored);
    }

    @GetMapping("/api/cases/{caseId}/evidence")
    @Operation(summary = "List all evidence items attached to a problem case")
    public ResponseEntity<List<EvidenceItemDto>> getEvidenceForCase(@PathVariable String caseId) {
        List<EvidenceItemDto> items = evidenceStorageService.getEvidenceForCase(caseId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/api/evidence/{evidenceId}/download")
    @Operation(summary = "Securely download an evidence file")
    public ResponseEntity<Resource> downloadEvidence(@PathVariable String evidenceId) {
        Resource resource = evidenceStorageService.loadAsResource(evidenceId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
