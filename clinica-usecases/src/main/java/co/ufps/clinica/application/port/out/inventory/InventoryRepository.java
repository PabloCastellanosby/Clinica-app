package co.ufps.clinica.application.port.out.inventory;

import co.ufps.clinica.domain.inventory.*;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Medicine save(Medicine medicine);
    Optional<Medicine> findById(MedicineId id);
    List<Medicine> findAllActive();
    StockLot saveLot(StockLot lot);
    Optional<StockLot> findLotById(StockLotId id);
    List<StockLot> findLotsByMedicine(MedicineId medicineId);
    StockMovement saveMovement(StockMovement movement);
    StockLot applyMovement(StockMovement movement);
    List<StockLot> findLotsBelowMinimum(BranchId branchId, int minimum);
    List<StockLot> findLotsExpiringBefore(BranchId branchId, LocalDate date);
}
