package co.ufps.clinica.adapters.communication;

import java.time.Instant;

public record PublishNoticeRequest(String title, String body, Instant expiresAt, String branchId) {}