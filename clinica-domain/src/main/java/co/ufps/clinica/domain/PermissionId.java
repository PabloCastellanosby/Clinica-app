package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Permission. */
public record PermissionId(UUID value) {
    public PermissionId {
        Objects.requireNonNull(value, "PermissionId no puede ser nulo");
    }

    public static PermissionId generate() {
        return new PermissionId(UUID.randomUUID());
    }

    public static PermissionId of(UUID value) {
        return new PermissionId(value);
    }
}