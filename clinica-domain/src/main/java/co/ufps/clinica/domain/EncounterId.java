package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Encounter. */
public record EncounterId(UUID value) {
    public EncounterId {
        Objects.requireNonNull(value, "EncounterId no puede ser nulo");
    }

    public static EncounterId generate() {
        return new EncounterId(UUID.randomUUID());
    }

    public static EncounterId of(UUID value) {
        return new EncounterId(value);
    }
}