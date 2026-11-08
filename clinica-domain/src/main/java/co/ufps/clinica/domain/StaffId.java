package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Staff. */
public record StaffId(UUID value) {
    public StaffId {
        Objects.requireNonNull(value, "StaffId no puede ser nulo");
    }

    public static StaffId generate() {
        return new StaffId(UUID.randomUUID());
    }

    public static StaffId of(UUID value) {
        return new StaffId(value);
    }
}