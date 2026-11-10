package co.ufps.clinica.domain.clinicalrecord;
import java.math.BigDecimal;
import java.time.Instant;
public record VitalSigns(VitalSignsId id, EncounterId encounterId, BigDecimal temperatureC, Integer systolic,
                         Integer diastolic, Integer heartRate, Integer respiratoryRate, BigDecimal weightKg,
                         BigDecimal heightCm, Instant measuredAt) {}
