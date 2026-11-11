package co.ufps.clinica.adapters.clinicalrecord;

import java.util.List;

public record IssuePrescriptionRequest(String encounterId, String patientId, String doctorId,
                                       String instructions, List<PrescriptionLineDto> lines) {
    public record PrescriptionLineDto(String medicine, String presentation, String dose,
                                      String frequency, int durationDays, int quantity) {}
}