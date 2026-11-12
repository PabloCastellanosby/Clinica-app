package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attachments")
public class AttachmentJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "encounter_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_attachments_encounter"))
    private co.ufps.clinica.frameworks.persistence.clinicalrecord.EncounterJpa encounterReference;
    @Column(name = "file_name", length = 500, nullable = false)
    private String fileName;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "storage_key", length = 500)
    private String storageKey;
    @Column(name = "size_bytes")
    private long sizeBytes;
    @Lob @Column(name = "content", nullable = false)
    private byte[] content;
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    public AttachmentJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEncounterId() { return encounterId; }
    public void setEncounterId(UUID encounterId) { this.encounterId = encounterId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public byte[] getContent() { return content == null ? null : content.clone(); }
    public void setContent(byte[] content) { this.content = content == null ? null : content.clone(); }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
}
