package com.sentinel.repository;

import com.sentinel.domain.model.EvidenceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceItemRepository extends JpaRepository<EvidenceItem, String> {
    List<EvidenceItem> findByProblemCaseIdOrderByUploadedAtAsc(String caseId);
}
