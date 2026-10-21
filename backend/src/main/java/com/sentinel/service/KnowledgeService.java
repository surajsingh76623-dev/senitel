package com.sentinel.service;

import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.model.KnowledgeEntry;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.KnowledgeEntryDto;
import com.sentinel.dto.PublishKnowledgeRequest;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.repository.KnowledgeEntryRepository;
import com.sentinel.repository.ProblemCaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeService.class);

    private final KnowledgeEntryRepository knowledgeEntryRepository;
    private final ProblemCaseRepository problemCaseRepository;
    private final AuditService auditService;

    public KnowledgeService(KnowledgeEntryRepository knowledgeEntryRepository,
                            ProblemCaseRepository problemCaseRepository,
                            AuditService auditService) {
        this.knowledgeEntryRepository = knowledgeEntryRepository;
        this.problemCaseRepository = problemCaseRepository;
        this.auditService = auditService;
    }

    @Transactional
    public KnowledgeEntryDto publishLesson(String caseId, PublishKnowledgeRequest request) {
        ProblemCase problemCase = null;
        if (caseId != null && !caseId.isBlank()) {
            problemCase = problemCaseRepository.findById(caseId).orElse(null);
        }

        log.info("Explicitly publishing approved anonymized knowledge entry: {}", request.getTitle());

        KnowledgeEntry entry = new KnowledgeEntry(
                caseId,
                request.getTitle().trim(),
                request.getCategory().trim(),
                request.getProblemSummary().trim(),
                request.getSolutionSummary().trim(),
                request.getVerificationSummary().trim(),
                request.getAnonymizedLessons().trim(),
                request.getTags(),
                request.getApprovedBy().trim()
        );
        entry.setIsApproved(true);
        entry.setApprovedAt(LocalDateTime.now());

        KnowledgeEntry saved = knowledgeEntryRepository.save(entry);

        if (problemCase != null) {
            auditService.logEvent(
                    problemCase,
                    AuditEventType.KNOWLEDGE_PUBLISHED,
                    request.getApprovedBy(),
                    "Explicitly approved and published anonymized outcome to Reusable Knowledge Library: " + saved.getTitle(),
                    "{\"knowledgeId\":\"" + saved.getId() + "\",\"approvedBy\":\"" + request.getApprovedBy() + "\"}"
            );
        }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<KnowledgeEntryDto> searchKnowledge(String query, String category) {
        List<KnowledgeEntry> entries;
        if ((query != null && !query.isBlank()) || (category != null && !category.isBlank())) {
            entries = knowledgeEntryRepository.searchKnowledge(
                    (query != null && !query.isBlank()) ? query.trim() : null,
                    (category != null && !category.isBlank()) ? category.trim() : null
            );
        } else {
            entries = knowledgeEntryRepository.findByIsApprovedTrueOrderByApprovedAtDesc();
        }

        return entries.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public KnowledgeEntryDto getKnowledgeById(String id) {
        KnowledgeEntry entry = knowledgeEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge entry not found with id: " + id));
        return toDto(entry);
    }

    private KnowledgeEntryDto toDto(KnowledgeEntry k) {
        KnowledgeEntryDto dto = new KnowledgeEntryDto();
        dto.setId(k.getId());
        dto.setSourceCaseId(k.getSourceCaseId());
        dto.setTitle(k.getTitle());
        dto.setCategory(k.getCategory());
        dto.setProblemSummary(k.getProblemSummary());
        dto.setSolutionSummary(k.getSolutionSummary());
        dto.setVerificationSummary(k.getVerificationSummary());
        dto.setAnonymizedLessons(k.getAnonymizedLessons());
        dto.setTags(k.getTags());
        dto.setIsApproved(k.getIsApproved());
        dto.setApprovedBy(k.getApprovedBy());
        dto.setApprovedAt(k.getApprovedAt());
        return dto;
    }
}
