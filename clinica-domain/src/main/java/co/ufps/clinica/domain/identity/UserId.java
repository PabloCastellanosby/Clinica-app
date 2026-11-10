package co.ufps.clinica.domain.identity;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para User. */
public record UserId(UUID value) {
    public UserId {
        Objects.requireNonNull(value, "UserId no puede ser nulo");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }
}