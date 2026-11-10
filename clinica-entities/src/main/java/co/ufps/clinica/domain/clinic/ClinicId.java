package co.ufps.clinica.domain.clinic;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Clinic. */
public record ClinicId(UUID value) {
    public ClinicId {
        Objects.requireNonNull(value, "ClinicId no puede ser nulo");
    }

    public static ClinicId generate() {
        return new ClinicId(UUID.randomUUID());
    }

    public static ClinicId of(UUID value) {
        return new ClinicId(value);
    }
}