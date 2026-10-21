package com.sentinel.dto;

import com.sentinel.domain.enums.VerificationStatus;
import com.sentinel.domain.enums.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateVerificationRunRequest {

    private String solutionId;

    @NotBlank(message = "Test procedure description is required")
    private String testProcedure;

    @NotBlank(message = "Expected result is required")
    private String expectedResult;

    private String actualResult;

    @NotNull(message = "Verification status is required")
    private VerificationStatus status;

    @NotNull(message = "Verification type is required")
    private VerificationType verificationType;

    private String evidenceNotes;
    private String recordedBy = "User";
    private Boolean isReproducible = true;

    public CreateVerificationRunRequest() {}

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

    public Boolean getIsReproducible() { return isReproducible; }
    public void setIsReproducible(Boolean isReproducible) { this.isReproducible = isReproducible; }
}
