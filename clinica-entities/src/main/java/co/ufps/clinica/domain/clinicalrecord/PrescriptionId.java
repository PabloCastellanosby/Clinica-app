package co.ufps.clinica.domain.clinicalrecord;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Prescription. */
public record PrescriptionId(UUID value) {
    public PrescriptionId {
        Objects.requireNonNull(value, "PrescriptionId no puede ser nulo");
    }

    public static PrescriptionId generate() {
        return new PrescriptionId(UUID.randomUUID());
    }

    public static PrescriptionId of(UUID value) {
        return new PrescriptionId(value);
    }
}