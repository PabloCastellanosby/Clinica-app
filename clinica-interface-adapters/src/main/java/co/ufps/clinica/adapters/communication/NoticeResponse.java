package co.ufps.clinica.adapters.communication;

import java.time.Instant;

public record NoticeResponse(String id, String title, String body, Instant publishedAt,
                             Instant expiresAt, String branchId) {}