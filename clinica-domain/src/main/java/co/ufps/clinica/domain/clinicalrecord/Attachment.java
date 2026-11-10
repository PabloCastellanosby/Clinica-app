package co.ufps.clinica.domain.clinicalrecord;
import java.time.Instant;
public record Attachment(AttachmentId id, EncounterId encounterId, String fileName, String contentType,
                         String storageKey, long sizeBytes, Instant uploadedAt) {}
