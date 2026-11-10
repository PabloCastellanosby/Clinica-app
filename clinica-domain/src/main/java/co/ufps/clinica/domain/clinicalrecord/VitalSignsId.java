package co.ufps.clinica.domain.clinicalrecord;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para VitalSigns. */
public record VitalSignsId(UUID value) {
    public VitalSignsId {
        Objects.requireNonNull(value, "VitalSignsId no puede ser nulo");
    }

    public static VitalSignsId generate() {
        return new VitalSignsId(UUID.randomUUID());
    }

    public static VitalSignsId of(UUID value) {
        return new VitalSignsId(value);
    }
}