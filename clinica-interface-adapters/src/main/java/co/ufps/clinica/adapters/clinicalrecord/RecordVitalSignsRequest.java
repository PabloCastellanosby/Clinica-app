package co.ufps.clinica.adapters.clinicalrecord;

import java.math.BigDecimal;

public record RecordVitalSignsRequest(String encounterId, BigDecimal temperatureC, Integer systolic,
                                      Integer diastolic, Integer heartRate, Integer respiratoryRate,
                                      BigDecimal weightKg, BigDecimal heightCm) {}