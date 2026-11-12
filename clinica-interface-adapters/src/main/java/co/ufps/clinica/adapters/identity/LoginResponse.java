package co.ufps.clinica.adapters.identity;

import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, Instant expiresAt,
                            String userId, String email, String role) {}
