package co.ufps.clinica.adapters.inventory;

import co.ufps.clinica.application.inventory.*;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.inventory.*;
import co.ufps.clinica.domain.identity.UserId;
import java.util.List;
import java.util.Objects;

public final class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = Objects.requireNonNull(service); }

    public MedicineResponse registerMedicine(RegisterMedicineRequest request) {
        Medicine m = service.registerMedicine(new RegisterMedicineCommand(
                request.name(), request.presentation(), request.dosageForm(), request.unit()));
        return presentMedicine(m);
    }

    public List<MedicineResponse> getAllActiveMedicines() {
        return service.getAllActiveMedicines().stream().map(this::presentMedicine).toList();
    }

    public StockLotResponse createStockLot(CreateStockLotRequest request) {
        StockLot l = service.createStockLot(new CreateStockLotCommand(
                BranchId.of(request.branchId()), MedicineId.of(request.medicineId()),
                request.lotNumber(), request.quantity(), request.expiresAt(), request.unitCost()));
        return presentStockLot(l);
    }

    public StockMovementResponse recordMovement(RecordMovementRequest request) {
        StockMovement m = service.recordMovement(new RecordMovementCommand(
                StockLotId.of(request.lotId()), StockMovementType.valueOf(request.type()),
                request.quantity(), request.reason(), UserId.of(request.actorId())));
        return presentMovement(m);
    }

    private MedicineResponse presentMedicine(Medicine m) {
        return new MedicineResponse(m.id().value().toString(), m.name(), m.presentation(),
                m.dosageForm(), m.unit(), m.isActive());
    }

    private StockLotResponse presentStockLot(StockLot l) {
        return new StockLotResponse(l.id().value().toString(), l.branchId().value().toString(),
                l.medicineId().value().toString(), l.lotNumber(), l.quantity(), l.expiresAt(), l.unitCost());
    }

    private StockMovementResponse presentMovement(StockMovement m) {
        return new StockMovementResponse(m.id().value().toString(), m.lotId().value().toString(),
                m.type().name(), m.quantity(), m.reason(), m.actorId().value().toString(), m.occurredAt());
    }
}