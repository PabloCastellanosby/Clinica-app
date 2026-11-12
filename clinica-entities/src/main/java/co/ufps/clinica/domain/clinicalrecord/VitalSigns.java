package co.ufps.clinica.domain.clinicalrecord;
import java.math.BigDecimal;
import java.time.Instant;
public record VitalSigns(VitalSignsId id, EncounterId encounterId, BigDecimal temperatureC, Integer systolic,
                         Integer diastolic, Integer heartRate, Integer respiratoryRate, BigDecimal weightKg,
                         BigDecimal heightCm, Instant measuredAt) {
    public VitalSigns {
        if (id == null || encounterId == null || measuredAt == null
                || (temperatureC == null && systolic == null && diastolic == null && heartRate == null
                    && respiratoryRate == null && weightKg == null && heightCm == null))
            throw new IllegalArgumentException("Los signos vitales deben incluir encuentro, fecha y al menos una medición");
        if (temperatureC != null && (temperatureC.compareTo(new BigDecimal("25.0")) < 0
                || temperatureC.compareTo(new BigDecimal("45.0")) > 0))
            throw new IllegalArgumentException("La temperatura debe estar entre 25 y 45 °C");
        if ((systolic != null && systolic <= 0) || (diastolic != null && diastolic <= 0)
                || (systolic != null && diastolic != null && systolic <= diastolic)
                || (heartRate != null && heartRate <= 0) || (respiratoryRate != null && respiratoryRate <= 0)
                || (weightKg != null && weightKg.signum() <= 0) || (heightCm != null && heightCm.signum() <= 0))
            throw new IllegalArgumentException("Los signos vitales deben ser positivos y la presión sistólica mayor a la diastólica");
    }
}
