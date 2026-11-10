package co.ufps.clinica.domain.catalog;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Service. */
public record ServiceId(UUID value) {
    public ServiceId {
        Objects.requireNonNull(value, "ServiceId no puede ser nulo");
    }

    public static ServiceId generate() {
        return new ServiceId(UUID.randomUUID());
    }

    public static ServiceId of(UUID value) {
        return new ServiceId(value);
    }
}