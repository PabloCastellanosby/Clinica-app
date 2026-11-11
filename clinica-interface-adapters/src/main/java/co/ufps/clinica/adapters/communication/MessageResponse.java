package co.ufps.clinica.adapters.communication;

import java.time.Instant;

public record MessageResponse(String id, String recipientId, String destination, String subject,
                              String template, String status, Instant createdAt, Instant sentAt) {}