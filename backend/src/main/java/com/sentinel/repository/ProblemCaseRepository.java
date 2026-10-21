package com.sentinel.repository;

import com.sentinel.domain.enums.ProblemCategory;
import com.sentinel.domain.enums.ProblemStatus;
import com.sentinel.domain.model.ProblemCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemCaseRepository extends JpaRepository<ProblemCase, String> {

    List<ProblemCase> findAllByOrderByCreatedAtDesc();

    List<ProblemCase> findByCategoryOrderByCreatedAtDesc(ProblemCategory category);

    List<ProblemCase> findByStatusOrderByCreatedAtDesc(ProblemStatus status);

    @Query("SELECT p FROM ProblemCase p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<ProblemCase> searchCases(
            @Param("query") String query,
            @Param("category") ProblemCategory category,
            @Param("status") ProblemStatus status
    );

    long countByStatus(ProblemStatus status);
}
