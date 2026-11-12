package co.ufps.clinica.domain.identity;

import java.util.Objects;
import java.util.UUID;

public record UserSessionId(UUID value) {
    public UserSessionId { Objects.requireNonNull(value, "El id de sesión es obligatorio"); }
    public static UserSessionId generate() { return new UserSessionId(UUID.randomUUID()); }
    public static UserSessionId of(UUID value) { return new UserSessionId(value); }
}
