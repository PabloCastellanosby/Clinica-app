package co.ufps.clinica.adapters.clinicalrecord;

import java.math.BigDecimal;

public record CreateEncounterRequest(String appointmentId, String patientId, String doctorId,
                                     String reason, String diagnosis, String notes, boolean patientVisible) {}