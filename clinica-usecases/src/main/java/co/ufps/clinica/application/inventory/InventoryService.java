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
    private final int minimumStock;
    public InventoryService(InventoryRepository repository, int minimumStock) {
        this.repository = Objects.requireNonNull(repository);
        if (minimumStock < 0) throw new IllegalArgumentException("El mínimo de inventario no puede ser negativo");
        this.minimumStock = minimumStock;
    }

    public Medicine registerMedicine(RegisterMedicineCommand command) {
        if (command == null || command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("El nombre del medicamento es obligatorio");
        Medicine medicine = new Medicine(MedicineId.generate(), command.name(), command.presentation(),
                command.dosageForm(), command.unit(), true);
        return repository.save(medicine);
    }

    public List<Medicine> getAllActiveMedicines() {
        return repository.findAllActive();
    }

    public StockLot createStockLot(CreateStockLotCommand command) {
        if (command == null || command.medicineId() == null || command.branchId() == null)
            throw new IllegalArgumentException("El lote debe indicar medicamento y sede");
        if (command.quantity() < 0 || command.unitCost() < 0 || command.lotNumber() == null || command.lotNumber().isBlank()) {
            throw new IllegalArgumentException("Lote, cantidad no negativa y costo no negativo son obligatorios");
        }
        repository.findById(command.medicineId()).filter(Medicine::active)
                .orElseThrow(() -> new NoSuchElementException("Medicamento activo no encontrado"));
        StockLot lot = new StockLot(StockLotId.generate(), command.branchId(), command.medicineId(),
                command.lotNumber().trim(), command.quantity(), command.expiresAt(), command.unitCost());
        return repository.saveLot(lot);
    }

    public StockMovement recordMovement(RecordMovementCommand command) {
        if (command == null || command.lotId() == null)
            throw new IllegalArgumentException("El movimiento debe indicar un lote");
        if (command.type() == null || command.actorId() == null) throw new IllegalArgumentException("Tipo de movimiento y responsable son obligatorios");
        if (command.type() == StockMovementType.ADJUSTMENT ? command.quantity() < 0 : command.quantity() <= 0)
            throw new IllegalArgumentException("La cantidad del movimiento debe ser válida para su tipo");
        StockMovement movement = new StockMovement(StockMovementId.generate(), command.lotId(),
                command.type(), command.quantity(), command.reason(), command.actorId(), Instant.now());
        repository.applyMovement(movement);
        return movement;
    }

    @Override
    public List<StockLot> belowMinimum(BranchId branchId) {
        return repository.findLotsBelowMinimum(branchId, minimumStock);
    }

    @Override
    public List<StockLot> expiringBefore(BranchId branchId, LocalDate date) {
        return repository.findLotsExpiringBefore(branchId, date);
    }

    @Override
    public StockLot adjust(StockMovement movement) {
        if (movement == null || (movement.type() == StockMovementType.ADJUSTMENT
                ? movement.quantity() < 0 : movement.quantity() <= 0))
            throw new IllegalArgumentException("El movimiento debe tener cantidad válida para su tipo");
        return repository.applyMovement(movement);
    }
}
