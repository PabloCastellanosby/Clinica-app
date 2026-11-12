package co.ufps.clinica.domain.clinicalrecord;
import java.time.Instant;
public record Attachment(AttachmentId id, EncounterId encounterId, String fileName, String contentType,
                         String storageKey, long sizeBytes, Instant uploadedAt, byte[] content) {
    public Attachment {
        if (id == null || encounterId == null || fileName == null || fileName.isBlank() || contentType == null
                || uploadedAt == null || content == null || content.length == 0 || sizeBytes != content.length)
            throw new IllegalArgumentException("El archivo clínico está incompleto");
        content = content.clone();
    }
    @Override public byte[] content() { return content.clone(); }
}
