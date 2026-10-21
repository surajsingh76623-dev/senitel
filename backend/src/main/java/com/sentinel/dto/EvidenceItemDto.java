package com.sentinel.dto;

import java.time.LocalDateTime;

public class EvidenceItemDto {

    private String id;
    private String caseId;
    private String originalFilename;
    private String mimeType;
    private Long fileSize;
    private String checksumSha256;
    private String fileSummary;
    private LocalDateTime uploadedAt;

    public EvidenceItemDto() {}

    public EvidenceItemDto(String id, String caseId, String originalFilename, String mimeType, Long fileSize, String checksumSha256, String fileSummary, LocalDateTime uploadedAt) {
        this.id = id;
        this.caseId = caseId;
        this.originalFilename = originalFilename;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.checksumSha256 = checksumSha256;
        this.fileSummary = fileSummary;
        this.uploadedAt = uploadedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCaseId() { return caseId; }
    public void setCaseId(String caseId) { this.caseId = caseId; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public String getFileSummary() { return fileSummary; }
    public void setFileSummary(String fileSummary) { this.fileSummary = fileSummary; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
