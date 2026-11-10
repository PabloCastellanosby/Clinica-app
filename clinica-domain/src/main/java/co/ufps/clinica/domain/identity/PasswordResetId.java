package co.ufps.clinica.domain.identity;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para PasswordReset. */
public record PasswordResetId(UUID value) {
    public PasswordResetId {
        Objects.requireNonNull(value, "PasswordResetId no puede ser nulo");
    }

    public static PasswordResetId generate() {
        return new PasswordResetId(UUID.randomUUID());
    }

    public static PasswordResetId of(UUID value) {
        return new PasswordResetId(value);
    }
}