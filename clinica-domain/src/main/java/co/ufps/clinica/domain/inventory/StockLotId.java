package co.ufps.clinica.domain.inventory;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para StockLot. */
public record StockLotId(UUID value) {
    public StockLotId {
        Objects.requireNonNull(value, "StockLotId no puede ser nulo");
    }

    public static StockLotId generate() {
        return new StockLotId(UUID.randomUUID());
    }

    public static StockLotId of(UUID value) {
        return new StockLotId(value);
    }
}