package com.sentinel.service;

import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.model.AuditEvent;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.AuditEventDto;
import com.sentinel.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public AuditEvent logEvent(ProblemCase problemCase, AuditEventType eventType, String actor, String description, String payloadJson) {
        AuditEvent event = new AuditEvent(problemCase, eventType, actor != null ? actor : "User", description, payloadJson);
        return auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEventDto> getTimelineForCase(String caseId) {
        return auditEventRepository.findByProblemCaseIdOrderByTimestampAsc(caseId).stream()
                .map(e -> new AuditEventDto(
                        e.getId(),
                        caseId,
                        e.getEventType(),
                        e.getActor(),
                        e.getDescription(),
                        e.getPayloadJson(),
                        e.getTimestamp()
                ))
                .toList();
    }
}
