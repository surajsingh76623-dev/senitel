package com.sentinel.repository;

import com.sentinel.domain.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {
    List<AuditEvent> findByProblemCaseIdOrderByTimestampAsc(String caseId);
}
