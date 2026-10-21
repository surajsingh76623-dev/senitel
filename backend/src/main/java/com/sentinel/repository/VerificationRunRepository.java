package com.sentinel.repository;

import com.sentinel.domain.model.VerificationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationRunRepository extends JpaRepository<VerificationRun, String> {
    List<VerificationRun> findByProblemCaseIdOrderByRecordedAtDesc(String caseId);
    List<VerificationRun> findByCandidateSolutionIdOrderByRecordedAtDesc(String solutionId);
}
