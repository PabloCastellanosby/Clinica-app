package co.ufps.clinica.application.identity;

import co.ufps.clinica.domain.identity.User;
import java.time.Instant;

public record IssuedSession(String accessToken, Instant expiresAt, User user) {}
