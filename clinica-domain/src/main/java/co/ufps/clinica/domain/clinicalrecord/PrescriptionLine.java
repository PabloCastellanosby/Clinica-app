package co.ufps.clinica.domain.clinicalrecord;
public record PrescriptionLine(PrescriptionLineId id, String medicine, String presentation, String dose,
                               String frequency, int durationDays, int quantity) {}
