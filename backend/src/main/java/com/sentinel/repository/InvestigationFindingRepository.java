package com.sentinel.repository;

import com.sentinel.domain.model.InvestigationFinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestigationFindingRepository extends JpaRepository<InvestigationFinding, String> {
    Optional<InvestigationFinding> findByProblemCaseId(String caseId);
}
