package co.ufps.clinica.adapters.inventory;

import java.time.Instant;

public record StockMovementResponse(String id, String lotId, String type, int quantity,
                                    String reason, String actorId, Instant occurredAt) {}