package co.ufps.clinica.application.inventory;

import co.ufps.clinica.domain.inventory.StockLotId;
import co.ufps.clinica.domain.inventory.StockMovementType;
import co.ufps.clinica.domain.identity.UserId;

public record RecordMovementCommand(StockLotId lotId, StockMovementType type,
                                    int quantity, String reason, UserId actorId) {}
