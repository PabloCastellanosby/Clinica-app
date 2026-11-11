package co.ufps.clinica.adapters.clinicalrecord;

import java.math.BigDecimal;
import java.time.Instant;

public record VitalSignsResponse(String id, String encounterId, BigDecimal temperatureC,
                                 Integer systolic, Integer diastolic, Integer heartRate,
                                 Integer respiratoryRate, BigDecimal weightKg, BigDecimal heightCm,
                                 Instant measuredAt) {}