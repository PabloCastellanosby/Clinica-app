package co.ufps.clinica.domain.clinic;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Holiday. */
public record HolidayId(UUID value) {
    public HolidayId {
        Objects.requireNonNull(value, "HolidayId no puede ser nulo");
    }

    public static HolidayId generate() {
        return new HolidayId(UUID.randomUUID());
    }

    public static HolidayId of(UUID value) {
        return new HolidayId(value);
    }
}