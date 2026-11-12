package co.ufps.clinica.domain.clinicalrecord;
import java.time.Instant;
public record ExamOrder(ExamOrderId id, EncounterId encounterId, String name, String instructions,
                        ExamOrderStatus status, Instant orderedAt, Instant completedAt, String result) {
    public ExamOrder {
        if (id == null || encounterId == null || name == null || name.isBlank() || status == null || orderedAt == null)
            throw new IllegalArgumentException("La orden de examen debe indicar encuentro, nombre, estado y fecha");
        if (result != null && result.length() > 5000)
            throw new IllegalArgumentException("El resultado supera la longitud permitida");
        if (status == ExamOrderStatus.COMPLETED && (completedAt == null || result == null || result.isBlank()))
            throw new IllegalArgumentException("Una orden completada debe incluir fecha y resultado");
    }
    public ExamOrder complete(String result, Instant completedAt) {
        if (status == ExamOrderStatus.COMPLETED || status == ExamOrderStatus.CANCELLED)
            throw new IllegalStateException("La orden de examen ya no se puede completar");
        if (result == null || result.isBlank() || completedAt == null || completedAt.isBefore(orderedAt))
            throw new IllegalArgumentException("El resultado y la fecha de finalización son obligatorios");
        return new ExamOrder(id, encounterId, name, instructions, ExamOrderStatus.COMPLETED, orderedAt, completedAt, result.trim());
    }
}
