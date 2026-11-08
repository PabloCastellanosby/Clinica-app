package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Office. */
public record OfficeId(UUID value) {
    public OfficeId {
        Objects.requireNonNull(value, "OfficeId no puede ser nulo");
    }

    public static OfficeId generate() {
        return new OfficeId(UUID.randomUUID());
    }

    public static OfficeId of(UUID value) {
        return new OfficeId(value);
    }
}