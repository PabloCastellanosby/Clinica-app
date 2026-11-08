package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Medicine. */
public record MedicineId(UUID value) {
    public MedicineId {
        Objects.requireNonNull(value, "MedicineId no puede ser nulo");
    }

    public static MedicineId generate() {
        return new MedicineId(UUID.randomUUID());
    }

    public static MedicineId of(UUID value) {
        return new MedicineId(value);
    }
}