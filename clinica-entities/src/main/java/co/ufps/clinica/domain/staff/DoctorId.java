package co.ufps.clinica.domain.staff;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Doctor. */
public record DoctorId(UUID value) {
    public DoctorId {
        Objects.requireNonNull(value, "DoctorId no puede ser nulo");
    }

    public static DoctorId generate() {
        return new DoctorId(UUID.randomUUID());
    }

    public static DoctorId of(UUID value) {
        return new DoctorId(value);
    }
}