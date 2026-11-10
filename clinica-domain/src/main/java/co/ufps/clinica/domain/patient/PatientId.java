package co.ufps.clinica.domain.patient;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Patient. */
public record PatientId(UUID value) {
    public PatientId {
        Objects.requireNonNull(value, "PatientId no puede ser nulo");
    }

    public static PatientId generate() {
        return new PatientId(UUID.randomUUID());
    }

    public static PatientId of(UUID value) {
        return new PatientId(value);
    }
}