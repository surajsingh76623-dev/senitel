package com.sentinel.repository;

import com.sentinel.domain.model.KnowledgeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeEntryRepository extends JpaRepository<KnowledgeEntry, String> {

    List<KnowledgeEntry> findByIsApprovedTrueOrderByApprovedAtDesc();

    List<KnowledgeEntry> findByCategoryAndIsApprovedTrueOrderByApprovedAtDesc(String category);

    @Query("SELECT k FROM KnowledgeEntry k WHERE k.isApproved = true AND " +
           "(:category IS NULL OR k.category = :category) AND " +
           "(:query IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(k.problemSummary) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(k.anonymizedLessons) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(k.tags) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY k.approvedAt DESC")
    List<KnowledgeEntry> searchKnowledge(
            @Param("query") String query,
            @Param("category") String category
    );
}
