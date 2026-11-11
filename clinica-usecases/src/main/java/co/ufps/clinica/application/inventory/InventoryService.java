package co.ufps.clinica.application.inventory;

import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.inventory.*;
import co.ufps.clinica.application.port.out.inventory.InventoryRepository;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public final class InventoryService implements StockGateway {
    private final InventoryRepository repository;
    public InventoryService(InventoryRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Medicine registerMedicine(RegisterMedicineCommand command) {
        Medicine medicine = new Medicine(MedicineId.generate(), command.name(), command.presentation(),
                command.dosageForm(), command.unit(), true);
        return repository.save(medicine);
    }

    public List<Medicine> getAllActiveMedicines() {
        return repository.findAllActive();
    }

    public StockLot createStockLot(CreateStockLotCommand command) {
        StockLot lot = new StockLot(StockLotId.generate(), command.branchId(), command.medicineId(),
                command.lotNumber(), command.quantity(), command.expiresAt(), command.unitCost());
        return repository.saveLot(lot);
    }

    public StockMovement recordMovement(RecordMovementCommand command) {
        StockMovement movement = new StockMovement(StockMovementId.generate(), command.lotId(),
                command.type(), command.quantity(), command.reason(), command.actorId(), Instant.now());
        repository.saveMovement(movement);
        return movement;
    }

    @Override
    public List<StockLot> belowMinimum(BranchId branchId) {
        return repository.findAllActive().stream().map(m ->
                repository.findLotsByMedicine(m.id())).flatMap(List::stream)
                .filter(l -> l.branchId().equals(branchId) && l.quantity() <= 10)
                .toList();
    }

    @Override
    public List<StockLot> expiringBefore(BranchId branchId, LocalDate date) {
        return repository.findAllActive().stream().map(m ->
                repository.findLotsByMedicine(m.id())).flatMap(List::stream)
                .filter(l -> l.branchId().equals(branchId) && l.expiresAt() != null && !l.expiresAt().isAfter(date))
                .toList();
    }

    @Override
    public StockLot adjust(StockMovement movement) {
        StockLot lot = repository.findLotById(movement.lotId())
                .orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));
        int newQty = switch (movement.type()) {
            case ENTRY -> lot.quantity() + movement.quantity();
            case EXIT, DISPENSATION -> lot.quantity() - movement.quantity();
            case ADJUSTMENT -> movement.quantity();
        };
        StockLot updated = new StockLot(lot.id(), lot.branchId(), lot.medicineId(),
                lot.lotNumber(), newQty, lot.expiresAt(), lot.unitCost());
        repository.saveLot(updated);
        repository.saveMovement(movement);
        return updated;
    }
}
