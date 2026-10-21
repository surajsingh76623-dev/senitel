package com.sentinel.dto;

import com.sentinel.domain.enums.AuditEventType;
import java.time.LocalDateTime;

public class AuditEventDto {

    private String id;
    private String caseId;
    private AuditEventType eventType;
    private String eventTypeDescription;
    private String actor;
    private String description;
    private String payloadJson;
    private LocalDateTime timestamp;

    public AuditEventDto() {}

    public AuditEventDto(String id, String caseId, AuditEventType eventType, String actor, String description, String payloadJson, LocalDateTime timestamp) {
        this.id = id;
        this.caseId = caseId;
        this.eventType = eventType;
        this.eventTypeDescription = eventType != null ? eventType.getDescription() : null;
        this.actor = actor;
        this.description = description;
        this.payloadJson = payloadJson;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) {
        this.eventType = eventType;
        this.eventTypeDescription = eventType != null ? eventType.getDescription() : null;
    }

    public String getEventTypeDescription() { return eventTypeDescription; }
    public void setEventTypeDescription(String eventTypeDescription) { this.eventTypeDescription = eventTypeDescription; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
