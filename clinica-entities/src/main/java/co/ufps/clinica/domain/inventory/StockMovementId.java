package co.ufps.clinica.domain.inventory;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para StockMovement. */
public record StockMovementId(UUID value) {
    public StockMovementId {
        Objects.requireNonNull(value, "StockMovementId no puede ser nulo");
    }

    public static StockMovementId generate() {
        return new StockMovementId(UUID.randomUUID());
    }

    public static StockMovementId of(UUID value) {
        return new StockMovementId(value);
    }
}