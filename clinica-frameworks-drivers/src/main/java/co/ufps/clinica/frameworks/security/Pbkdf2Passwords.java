package co.ufps.clinica.frameworks.security;

import co.ufps.clinica.application.identity.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@ApplicationScoped
public class Pbkdf2Passwords implements AccountService.Passwords {
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;
    private final SecureRandom random = new SecureRandom();

    @Override public String hash(String plainText) {
        if (plainText == null || plainText.length() > 128) throw new IllegalArgumentException("La contraseña supera el máximo de 128 caracteres");
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        byte[] derived = derive(plainText, salt, ITERATIONS);
        return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derived);
    }

    @Override public boolean matches(String plainText, String encodedHash) {
        if (plainText == null || plainText.length() > 128 || encodedHash == null) return false;
        try {
            String[] parts = encodedHash.split("\\$", -1);
            if (parts.length != 4 || !parts[0].equals("pbkdf2-sha256")) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 1_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(plainText, salt, iterations));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private byte[] derive(String plainText, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt, iterations, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
