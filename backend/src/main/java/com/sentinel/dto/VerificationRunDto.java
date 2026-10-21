package com.sentinel.dto;

import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.enums.VerificationType;
import java.time.LocalDateTime;

public class VerificationRunDto {

    private String id;
    private String caseId;
    private String solutionId;
    private String testProcedure;
    private String expectedResult;
    private String actualResult;
    private VerificationStatus status;
    private VerificationType verificationType;
    private String evidenceNotes;
    private String recordedBy;
    private LocalDateTime recordedAt;
    private Boolean isReproducible;

    public VerificationRunDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getSolutionId() { return solutionId; }
    public void setSolutionId(String solutionId) { this.solutionId = solutionId; }

    public String getTestProcedure() { return testProcedure; }
    public void setTestProcedure(String testProcedure) { this.testProcedure = testProcedure; }

    public String getExpectedResult() { return expectedResult; }
    public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }

    public String getActualResult() { return actualResult; }
    public void setActualResult(String actualResult) { this.actualResult = actualResult; }

    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }

    public VerificationType getVerificationType() { return verificationType; }
    public void setVerificationType(VerificationType verificationType) { this.verificationType = verificationType; }

    public String getEvidenceNotes() { return evidenceNotes; }
    public void setEvidenceNotes(String evidenceNotes) { this.evidenceNotes = evidenceNotes; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Boolean getIsReproducible() { return isReproducible; }
    public void setIsReproducible(Boolean isReproducible) { this.isReproducible = isReproducible; }
}
