package fr.datashare.file;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// Am.Yo : entité liée à la table files (Flyway V1)
@Entity
@Table(name = "files")
public class StoredFile {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "storage_key", nullable = false, unique = true, length = 64)
    private String storageKey;

    @Column(name = "download_token", nullable = false, unique = true, length = 32)
    private String downloadToken;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "purged_at")
    private Instant purgedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_tags", joinColumns = @JoinColumn(name = "file_id"))
    @Column(name = "label", length = 30)
    private Set<String> tags = new HashSet<>();

    protected StoredFile() {
    }

    public StoredFile(
            UUID id,
            UUID ownerId,
            String originalName,
            String mimeType,
            long sizeBytes,
            String storageKey,
            String downloadToken,
            String passwordHash,
            Instant expiresAt,
            Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.storageKey = storageKey;
        this.downloadToken = downloadToken;
        this.passwordHash = passwordHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getDownloadToken() {
        return downloadToken;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPurgedAt() {
        return purgedAt;
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean isProtected() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt) || purgedAt != null;
    }
}
