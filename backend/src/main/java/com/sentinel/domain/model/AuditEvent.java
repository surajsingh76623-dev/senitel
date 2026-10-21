package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sentinel.domain.enums.AuditEventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private ProblemCase problemCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50, nullable = false)
    private AuditEventType eventType;

    @Column(name = "actor", length = 100, nullable = false)
    private String actor;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AuditEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.actor = "User";
    }

    public AuditEvent(ProblemCase problemCase, AuditEventType eventType, String actor, String description, String payloadJson) {
        this();
        this.problemCase = problemCase;
        this.eventType = eventType;
        this.actor = actor != null ? actor : "User";
        this.description = description;
        this.payloadJson = payloadJson;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
