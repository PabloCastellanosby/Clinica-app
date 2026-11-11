package co.ufps.clinica.adapters.clinicalrecord;

import java.time.Instant;

public record MedicalHistoryResponse(String id, String patientId, String type, String description,
                                     Instant recordedAt, String recordedBy) {}