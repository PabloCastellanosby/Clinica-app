package co.ufps.clinica.domain.clinic;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para OpeningHours. */
public record OpeningHoursId(UUID value) {
    public OpeningHoursId {
        Objects.requireNonNull(value, "OpeningHoursId no puede ser nulo");
    }

    public static OpeningHoursId generate() {
        return new OpeningHoursId(UUID.randomUUID());
    }

    public static OpeningHoursId of(UUID value) {
        return new OpeningHoursId(value);
    }
}