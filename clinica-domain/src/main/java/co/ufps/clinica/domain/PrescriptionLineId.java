package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para PrescriptionLine. */
public record PrescriptionLineId(UUID value) {
    public PrescriptionLineId {
        Objects.requireNonNull(value, "PrescriptionLineId no puede ser nulo");
    }

    public static PrescriptionLineId generate() {
        return new PrescriptionLineId(UUID.randomUUID());
    }

    public static PrescriptionLineId of(UUID value) {
        return new PrescriptionLineId(value);
    }
}