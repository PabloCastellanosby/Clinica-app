package co.ufps.clinica.domain.inventory;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
public record StockMovement(StockMovementId id, StockLotId lotId, StockMovementType type, int quantity,
                            String reason, UserId actorId, Instant occurredAt) {
    public StockMovement {
        boolean invalidQuantity = type == StockMovementType.ADJUSTMENT ? quantity < 0 : quantity <= 0;
        if (id == null || lotId == null || type == null || invalidQuantity || actorId == null || occurredAt == null)
            throw new IllegalArgumentException("El movimiento debe tener lote, tipo, cantidad válida, responsable y fecha");
    }
}
