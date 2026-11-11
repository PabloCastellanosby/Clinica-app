package co.ufps.clinica.adapters.inventory;

public record RecordMovementRequest(String lotId, String type, int quantity, String reason, String actorId) {}