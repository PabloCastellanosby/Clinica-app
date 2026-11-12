package co.ufps.clinica.frameworks.persistence.communication;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "patient_notices", indexes = @Index(name = "idx_notice_branch", columnList = "branch_id"))
public class PatientNoticeJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "title", length = 500, nullable = false)
    private String title;
    @Column(name = "body", length = 5000, nullable = false)
    private String body;
    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "branch_id")
    private UUID branchId;

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_patient_notices_branch"))
    private co.ufps.clinica.frameworks.persistence.clinic.BranchJpa branchReference;
    public PatientNoticeJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
}
