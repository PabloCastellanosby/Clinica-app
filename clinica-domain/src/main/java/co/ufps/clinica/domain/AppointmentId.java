package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Appointment. */
public record AppointmentId(UUID value) {
    public AppointmentId {
        Objects.requireNonNull(value, "AppointmentId no puede ser nulo");
    }

    public static AppointmentId generate() {
        return new AppointmentId(UUID.randomUUID());
    }

    public static AppointmentId of(UUID value) {
        return new AppointmentId(value);
    }
}