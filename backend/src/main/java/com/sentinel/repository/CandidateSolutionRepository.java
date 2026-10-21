package com.sentinel.repository;

import com.sentinel.domain.model.CandidateSolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateSolutionRepository extends JpaRepository<CandidateSolution, String> {
    List<CandidateSolution> findByProblemCaseIdOrderBySolutionIndexAsc(String caseId);
    void deleteByProblemCaseId(String caseId);
}
