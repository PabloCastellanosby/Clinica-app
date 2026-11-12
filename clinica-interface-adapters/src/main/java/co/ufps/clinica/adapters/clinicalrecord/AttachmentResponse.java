package co.ufps.clinica.adapters.clinicalrecord;

import java.time.Instant;

public record AttachmentResponse(String id, String encounterId, String fileName, String contentType,
                                 long sizeBytes, Instant uploadedAt) {}
