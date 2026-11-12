package co.ufps.clinica.domain.clinicalrecord;
public record PrescriptionLine(PrescriptionLineId id, String medicine, String presentation, String dose,
                               String frequency, int durationDays, int quantity) {
    public PrescriptionLine {
        if (id == null || medicine == null || medicine.isBlank() || durationDays <= 0 || quantity <= 0)
            throw new IllegalArgumentException("Cada línea de receta requiere medicamento, duración y cantidad positivas");
    }
}
