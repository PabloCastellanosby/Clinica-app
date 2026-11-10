package co.ufps.clinica.domain.clinicalrecord;
import java.time.Instant;
public record ExamOrder(ExamOrderId id, EncounterId encounterId, String name, String instructions,
                        ExamOrderStatus status, Instant orderedAt, Instant completedAt, String result) {}
