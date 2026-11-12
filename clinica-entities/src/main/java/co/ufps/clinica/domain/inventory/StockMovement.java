package co.ufps.clinica.domain.inventory;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
public record StockMovement(StockMovementId id, StockLotId lotId, StockMovementType type, int quantity,
                            String reason, UserId actorId, Instant occurredAt) {
    public StockMovement {
        if (id == null || lotId == null || type == null || quantity <= 0 || actorId == null || occurredAt == null)
            throw new IllegalArgumentException("El movimiento debe tener lote, tipo, cantidad positiva, responsable y fecha");
    }
}
