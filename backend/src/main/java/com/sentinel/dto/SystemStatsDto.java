package com.sentinel.dto;

import java.util.Map;

public class SystemStatsDto {

    private long totalCases;
    private long draftCases;
    private long investigatingCases;
    private long proposedCases;
    private long inProgressCases;
    private long userSuccessCases;
    private long verifiedCases;
    private long failedCases;
    private long needsEvidenceCases;
    private long knowledgeEntriesCount;
    private long evidenceFilesCount;
    private Map<String, Long> casesByCategory;

    public SystemStatsDto() {}

    public long getTotalCases() { return totalCases; }
    public void setTotalCases(long totalCases) { this.totalCases = totalCases; }

    public long getDraftCases() { return draftCases; }
    public void setDraftCases(long draftCases) { this.draftCases = draftCases; }

    public long getInvestigatingCases() { return investigatingCases; }
    public void setInvestigatingCases(long investigatingCases) { this.investigatingCases = investigatingCases; }

    public long getProposedCases() { return proposedCases; }
    public void setProposedCases(long proposedCases) { this.proposedCases = proposedCases; }

    public long getInProgressCases() { return inProgressCases; }
    public void setInProgressCases(long inProgressCases) { this.inProgressCases = inProgressCases; }

    public long getUserSuccessCases() { return userSuccessCases; }
    public void setUserSuccessCases(long userSuccessCases) { this.userSuccessCases = userSuccessCases; }

    public long getVerifiedCases() { return verifiedCases; }
    public void setVerifiedCases(long verifiedCases) { this.verifiedCases = verifiedCases; }

    public long getFailedCases() { return failedCases; }
    public void setFailedCases(long failedCases) { this.failedCases = failedCases; }

    public long getNeedsEvidenceCases() { return needsEvidenceCases; }
    public void setNeedsEvidenceCases(long needsEvidenceCases) { this.needsEvidenceCases = needsEvidenceCases; }

    public long getKnowledgeEntriesCount() { return knowledgeEntriesCount; }
    public void setKnowledgeEntriesCount(long knowledgeEntriesCount) { this.knowledgeEntriesCount = knowledgeEntriesCount; }

    public long getEvidenceFilesCount() { return evidenceFilesCount; }
    public void setEvidenceFilesCount(long evidenceFilesCount) { this.evidenceFilesCount = evidenceFilesCount; }

    public Map<String, Long> getCasesByCategory() { return casesByCategory; }
    public void setCasesByCategory(Map<String, Long> casesByCategory) { this.casesByCategory = casesByCategory; }
}
