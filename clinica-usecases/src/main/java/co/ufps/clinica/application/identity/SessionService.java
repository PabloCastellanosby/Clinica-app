package co.ufps.clinica.application.identity;

import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.domain.identity.User;
import co.ufps.clinica.domain.identity.UserSession;
import co.ufps.clinica.domain.identity.UserSessionId;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/** Sesiones opacas: el token en claro solo se entrega al cliente y nunca se persiste. */
public final class SessionService {
    private static final Duration SESSION_LIFETIME = Duration.ofMinutes(30);
    private final UserRepository repository;
    private final SecureRandom random = new SecureRandom();

    public SessionService(UserRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public IssuedSession issue(User user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant now = Instant.now();
        Instant expiresAt = now.plus(SESSION_LIFETIME);
        repository.saveSession(new UserSession(UserSessionId.generate(), user.id(), hash(token), now, expiresAt, null));
        return new IssuedSession(token, expiresAt, user);
    }

    public Optional<AuthenticatedUser> authenticate(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return repository.findSessionByTokenHash(hash(token))
                .filter(session -> session.isActiveAt(Instant.now()))
                .flatMap(session -> repository.findById(session.userId()))
                .filter(User::active)
                .map(user -> new AuthenticatedUser(user.id(), user.email(), user.role()));
    }

    public void revoke(String token) {
        if (token == null || token.isBlank()) return;
        repository.findSessionByTokenHash(hash(token)).ifPresent(session -> repository.saveSession(session.revoke(Instant.now())));
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo calcular la huella de la sesión", ex);
        }
    }
}
