package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para MedicalHistory. */
public record MedicalHistoryId(UUID value) {
    public MedicalHistoryId {
        Objects.requireNonNull(value, "MedicalHistoryId no puede ser nulo");
    }

    public static MedicalHistoryId generate() {
        return new MedicalHistoryId(UUID.randomUUID());
    }

    public static MedicalHistoryId of(UUID value) {
        return new MedicalHistoryId(value);
    }
}