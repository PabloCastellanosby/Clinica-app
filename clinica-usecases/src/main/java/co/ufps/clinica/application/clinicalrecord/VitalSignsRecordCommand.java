package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.clinicalrecord.EncounterId;
import java.math.BigDecimal;

public record VitalSignsRecordCommand(EncounterId encounterId, BigDecimal temperatureC, Integer systolic,
                                      Integer diastolic, Integer heartRate, Integer respiratoryRate,
                                      BigDecimal weightKg, BigDecimal heightCm) {}
