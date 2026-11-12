package co.ufps.clinica.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class StockMovementTest {
    @Test void allowsZeroAsTheResultOfAStockCountAdjustment() {
        assertDoesNotThrow(() -> new StockMovement(StockMovementId.generate(), StockLotId.generate(),
                StockMovementType.ADJUSTMENT, 0, "Conteo físico", UserId.generate(), Instant.now()));
    }

    @Test void requiresPositiveQuantityForEntriesExitsAndDispensations() {
        for (StockMovementType type : new StockMovementType[] {
                StockMovementType.ENTRY, StockMovementType.EXIT, StockMovementType.DISPENSATION}) {
            assertThrows(IllegalArgumentException.class, () -> new StockMovement(StockMovementId.generate(),
                    StockLotId.generate(), type, 0, null, UserId.generate(), Instant.now()));
        }
    }
}
