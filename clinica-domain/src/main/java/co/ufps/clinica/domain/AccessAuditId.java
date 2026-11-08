package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para AccessAudit. */
public record AccessAuditId(UUID value) {
    public AccessAuditId {
        Objects.requireNonNull(value, "AccessAuditId no puede ser nulo");
    }

    public static AccessAuditId generate() {
        return new AccessAuditId(UUID.randomUUID());
    }

    public static AccessAuditId of(UUID value) {
        return new AccessAuditId(value);
    }
}