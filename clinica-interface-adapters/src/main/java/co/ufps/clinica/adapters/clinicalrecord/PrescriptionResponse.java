package co.ufps.clinica.adapters.clinicalrecord;

import java.time.Instant;
import java.util.List;

public record PrescriptionResponse(String id, String encounterId, String patientId, String doctorId,
                                   Instant issuedAt, String instructions, List<String> lines) {}