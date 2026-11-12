package co.ufps.clinica.application.identity;

import co.ufps.clinica.application.port.out.communication.EmailGateway;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.domain.identity.PasswordReset;
import co.ufps.clinica.domain.identity.PasswordResetId;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public final class PasswordResetService {
    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(20);
    private final UserRepository users;
    private final AccountService.Passwords passwords;
    private final EmailGateway email;
    private final String resetUrl;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository users, AccountService.Passwords passwords, EmailGateway email, String resetUrl) {
        this.users = Objects.requireNonNull(users);
        this.passwords = Objects.requireNonNull(passwords);
        this.email = Objects.requireNonNull(email);
        this.resetUrl = Objects.requireNonNull(resetUrl);
    }

    public void request(String address) {
        String normalized = address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
        var user = users.findByEmail(normalized).filter(co.ufps.clinica.domain.identity.User::active).orElse(null);
        if (user == null) return;
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        Instant now = Instant.now();
        PasswordReset reset = new PasswordReset(PasswordResetId.generate(), user.id(), hash(token), now.plus(TOKEN_LIFETIME), null);
        users.savePasswordReset(reset);
        String link = resetUrl + (resetUrl.contains("?") ? "&" : "?") + "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        try {
            email.send(user.email(), "Recuperación de contraseña", "Solicitaste cambiar tu contraseña. El enlace vence en 20 minutos: " + link);
        } catch (RuntimeException ignored) {
            // La respuesta pública no revela si la cuenta existe ni el estado del proveedor de correo.
        }
    }

    public void reset(String token, String newPassword) {
        if (token == null || token.isBlank() || newPassword == null || newPassword.length() < 10 || newPassword.length() > 128)
            throw new IllegalArgumentException("El token es obligatorio y la contraseña debe tener entre 10 y 128 caracteres");
        PasswordReset reset = users.findPasswordReset(hash(token)).filter(r -> r.isUsableAt(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación no es válido o venció"));
        var current = users.findById(reset.userId()).filter(co.ufps.clinica.domain.identity.User::active)
                .orElseThrow(() -> new IllegalArgumentException("La cuenta no está disponible"));
        var updated = new co.ufps.clinica.domain.identity.User(current.id(), current.email(), passwords.hash(newPassword),
                current.role(), current.active(), current.createdAt(), current.lastLoginAt());
        users.consumePasswordReset(reset.markUsed(Instant.now()), updated);
    }

    private String hash(String token) {
        try {
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo validar el token de recuperación", ex);
        }
    }
}
