package co.ufps.clinica.domain.clinicalrecord;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para ExamOrder. */
public record ExamOrderId(UUID value) {
    public ExamOrderId {
        Objects.requireNonNull(value, "ExamOrderId no puede ser nulo");
    }

    public static ExamOrderId generate() {
        return new ExamOrderId(UUID.randomUUID());
    }

    public static ExamOrderId of(UUID value) {
        return new ExamOrderId(value);
    }
}