package co.ufps.clinica.adapters.clinicalrecord;

import java.time.Instant;

public record EncounterResponse(String id, String appointmentId, String patientId, String doctorId,
                                Instant occurredAt, String reason, String diagnosis, String notes, boolean patientVisible) {}