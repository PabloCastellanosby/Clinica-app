package co.ufps.clinica.domain.appointment;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para AppointmentReminder. */
public record AppointmentReminderId(UUID value) {
    public AppointmentReminderId {
        Objects.requireNonNull(value, "AppointmentReminderId no puede ser nulo");
    }

    public static AppointmentReminderId generate() {
        return new AppointmentReminderId(UUID.randomUUID());
    }

    public static AppointmentReminderId of(UUID value) {
        return new AppointmentReminderId(value);
    }
}