package com.sentinel.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidence_items")
public class EvidenceItem {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private ProblemCase problemCase;

    @Column(name = "original_filename", length = 255, nullable = false)
    private String originalFilename;

    @Column(name = "storage_filename", length = 255, nullable = false)
    private String storageFilename;

    @Column(name = "mime_type", length = 100, nullable = false)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "checksum_sha256", length = 64)
    private String checksumSha256;

    @Column(name = "file_summary", columnDefinition = "TEXT")
    private String fileSummary;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    public EvidenceItem() {
        this.id = UUID.randomUUID().toString();
        this.uploadedAt = LocalDateTime.now();
    }

    public EvidenceItem(ProblemCase problemCase, String originalFilename, String storageFilename, String mimeType, Long fileSize, String checksumSha256) {
        this();
        this.problemCase = problemCase;
        this.originalFilename = originalFilename;
        this.storageFilename = storageFilename;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.checksumSha256 = checksumSha256;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ProblemCase getProblemCase() { return problemCase; }
    public void setProblemCase(ProblemCase problemCase) { this.problemCase = problemCase; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getStorageFilename() { return storageFilename; }
    public void setStorageFilename(String storageFilename) { this.storageFilename = storageFilename; }

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
