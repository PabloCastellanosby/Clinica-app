package co.ufps.clinica.domain.catalog;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Specialty. */
public record SpecialtyId(UUID value) {
    public SpecialtyId {
        Objects.requireNonNull(value, "SpecialtyId no puede ser nulo");
    }

    public static SpecialtyId generate() {
        return new SpecialtyId(UUID.randomUUID());
    }

    public static SpecialtyId of(UUID value) {
        return new SpecialtyId(value);
    }
}