package co.ufps.clinica.domain.staff;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para DoctorAvailability. */
public record DoctorAvailabilityId(UUID value) {
    public DoctorAvailabilityId {
        Objects.requireNonNull(value, "DoctorAvailabilityId no puede ser nulo");
    }

    public static DoctorAvailabilityId generate() {
        return new DoctorAvailabilityId(UUID.randomUUID());
    }

    public static DoctorAvailabilityId of(UUID value) {
        return new DoctorAvailabilityId(value);
    }
}