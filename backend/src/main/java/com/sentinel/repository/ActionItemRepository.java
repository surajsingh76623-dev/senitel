package com.sentinel.repository;

import com.sentinel.domain.model.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, String> {
    List<ActionItem> findByProblemCaseIdOrderByStepNumberAsc(String caseId);
    List<ActionItem> findByCandidateSolutionIdOrderByStepNumberAsc(String solutionId);
    void deleteByProblemCaseId(String caseId);
}
