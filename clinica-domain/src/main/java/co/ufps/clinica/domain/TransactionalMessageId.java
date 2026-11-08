package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para TransactionalMessage. */
public record TransactionalMessageId(UUID value) {
    public TransactionalMessageId {
        Objects.requireNonNull(value, "TransactionalMessageId no puede ser nulo");
    }

    public static TransactionalMessageId generate() {
        return new TransactionalMessageId(UUID.randomUUID());
    }

    public static TransactionalMessageId of(UUID value) {
        return new TransactionalMessageId(value);
    }
}