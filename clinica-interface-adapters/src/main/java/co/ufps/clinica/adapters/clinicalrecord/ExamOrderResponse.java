package co.ufps.clinica.adapters.clinicalrecord;

import java.time.Instant;

public record ExamOrderResponse(String id, String encounterId, String name, String instructions,
                                String status, Instant orderedAt, Instant completedAt, String result) {}