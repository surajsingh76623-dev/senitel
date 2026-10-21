package com.sentinel.controller;

import com.sentinel.config.AiProperties;
import com.sentinel.config.StorageProperties;
import com.sentinel.dto.SystemSettingsDto;
import com.sentinel.dto.SystemStatsDto;
import com.sentinel.service.ProblemCaseService;
import com.sentinel.service.ai.AiInvestigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/system")
@Tag(name = "System Diagnostics & Settings", description = "System metrics, AI provider status, and configuration inspection")
public class SystemController {

    private final ProblemCaseService problemCaseService;
    private final AiProperties aiProperties;
    private final StorageProperties storageProperties;
    private final AiInvestigationService aiInvestigationService;
    private final Environment environment;

    public SystemController(
            ProblemCaseService problemCaseService,
            AiProperties aiProperties,
            StorageProperties storageProperties,
            AiInvestigationService aiInvestigationService,
            Environment environment) {
        this.problemCaseService = problemCaseService;
        this.aiProperties = aiProperties;
        this.storageProperties = storageProperties;
        this.aiInvestigationService = aiInvestigationService;
        this.environment = environment;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated case metrics and system distribution statistics")
    public ResponseEntity<SystemStatsDto> getSystemStats() {
        SystemStatsDto stats = problemCaseService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/settings")
    @Operation(summary = "Get active AI provider status, model configuration, and storage parameters (secrets masked)")
    public ResponseEntity<SystemSettingsDto> getSettings() {
        SystemSettingsDto dto = new SystemSettingsDto();
        dto.setAiProvider(aiInvestigationService.getProviderName());
        dto.setAiModel(aiProperties.getModel());
        dto.setHasApiKey(aiProperties.hasApiKey());
        dto.setMaskedApiKey(maskApiKey(aiProperties.getApiKey()));
        dto.setStorageLocation(storageProperties.getLocation());
        dto.setMaxFileSizeMb(storageProperties.getMaxFileSizeMb());
        dto.setDemoMode(aiInvestigationService.isDemoMode());
        dto.setDatabaseProfile(Arrays.toString(environment.getActiveProfiles()));
        dto.setAppVersion("1.0.0-PROD-CANDIDATE");
        return ResponseEntity.ok(dto);
    }

    private String maskApiKey(String key) {
        if (key == null || key.isBlank()) return "None (Operating in Demo Mode)";
        if (key.length() <= 8) return "••••••••";
        return key.substring(0, 4) + "••••••••" + key.substring(key.length() - 4);
    }
}
