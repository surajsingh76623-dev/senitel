package com.sentinel.service;

import com.sentinel.config.StorageProperties;
import com.sentinel.domain.enums.AuditEventType;
import com.sentinel.domain.model.EvidenceItem;
import com.sentinel.domain.model.ProblemCase;
import com.sentinel.dto.EvidenceItemDto;
import com.sentinel.exception.ResourceNotFoundException;
import com.sentinel.exception.StorageException;
import com.sentinel.repository.EvidenceItemRepository;
import com.sentinel.repository.ProblemCaseRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceStorageService {

    private static final Logger log = LoggerFactory.getLogger(EvidenceStorageService.class);

    private final StorageProperties storageProperties;
    private final EvidenceItemRepository evidenceItemRepository;
    private final ProblemCaseRepository problemCaseRepository;
    private final AuditService auditService;
    private Path rootStoragePath;

    public EvidenceStorageService(StorageProperties storageProperties,
                                  EvidenceItemRepository evidenceItemRepository,
                                  ProblemCaseRepository problemCaseRepository,
                                  AuditService auditService) {
        this.storageProperties = storageProperties;
        this.evidenceItemRepository = evidenceItemRepository;
        this.problemCaseRepository = problemCaseRepository;
        this.auditService = auditService;
    }

    @PostConstruct
    public void init() {
        this.rootStoragePath = Paths.get(storageProperties.getLocation()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootStoragePath);
            log.info("Initialized evidence file storage directory at: {}", this.rootStoragePath);
        } catch (IOException e) {
            throw new StorageException("Could not create storage directory: " + this.rootStoragePath, e);
        }
    }

    @Transactional
    public EvidenceItemDto storeEvidence(String caseId, MultipartFile file, String summary) {
        ProblemCase problemCase = problemCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem case not found with id: " + caseId));

        if (file.isEmpty()) {
            throw new StorageException("Cannot upload empty file");
        }

        long maxBytes = (long) storageProperties.getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new StorageException("File size exceeds maximum allowed limit of " + storageProperties.getMaxFileSizeMb() + "MB");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_evidence";
        String cleanOriginalName = Paths.get(originalFilename).getFileName().toString();
        String extension = getFileExtension(cleanOriginalName);

        if (!storageProperties.getAllowedExtensions().contains(extension.toLowerCase())) {
            throw new StorageException("File extension ." + extension + " is not permitted. Allowed: " + storageProperties.getAllowedExtensions());
        }

        // Generate safe random storage filename to prevent collisions and path traversal
        String storageFilename = UUID.randomUUID().toString() + "_" + cleanOriginalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path destination = this.rootStoragePath.resolve(storageFilename).normalize();

        // Path traversal guard
        if (!destination.startsWith(this.rootStoragePath)) {
            throw new StorageException("Invalid storage path target: traversal detected");
        }

        String checksum;
        try (InputStream inputStream = file.getInputStream()) {
            checksum = calculateSha256(file);
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + cleanOriginalName, e);
        }

        String mimeType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        EvidenceItem item = new EvidenceItem(problemCase, cleanOriginalName, storageFilename, mimeType, file.getSize(), checksum);
        item.setFileSummary(summary);
        EvidenceItem saved = evidenceItemRepository.save(item);

        auditService.logEvent(
                problemCase,
                AuditEventType.EVIDENCE_UPLOADED,
                "User",
                "Uploaded evidence file: " + cleanOriginalName + " (" + (file.getSize() / 1024) + " KB)",
                "{\"evidenceId\":\"" + saved.getId() + "\",\"filename\":\"" + cleanOriginalName + "\",\"checksum\":\"" + checksum + "\"}"
        );

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EvidenceItemDto> getEvidenceForCase(String caseId) {
        return evidenceItemRepository.findByProblemCaseIdOrderByUploadedAtAsc(caseId).stream()
                .map(this::toDto)
                .toList();
    }

    public Resource loadAsResource(String evidenceId) {
        EvidenceItem item = evidenceItemRepository.findById(evidenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence item not found with id: " + evidenceId));

        try {
            Path file = rootStoragePath.resolve(item.getStorageFilename()).normalize();
            if (!file.startsWith(rootStoragePath)) {
                throw new StorageException("Path traversal attempt detected");
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("Could not read file: " + item.getOriginalFilename());
            }
        } catch (Exception e) {
            throw new StorageException("Could not read file: " + item.getOriginalFilename(), e);
        }
    }

    public EvidenceItemDto toDto(EvidenceItem item) {
        return new EvidenceItemDto(
                item.getId(),
                item.getProblemCase() != null ? item.getProblemCase().getId() : null,
                item.getOriginalFilename(),
                item.getMimeType(),
                item.getFileSize(),
                item.getChecksumSha256(),
                item.getFileSummary(),
                item.getUploadedAt()
        );
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1 || lastIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastIndex + 1);
    }

    private String calculateSha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.warn("Failed to calculate SHA-256 checksum: {}", e.getMessage());
            return "UNKNOWN";
        }
    }
}
