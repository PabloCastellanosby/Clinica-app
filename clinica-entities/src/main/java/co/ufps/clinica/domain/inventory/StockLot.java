package co.ufps.clinica.domain.inventory;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.LocalDate;
public record StockLot(StockLotId id, BranchId branchId, MedicineId medicineId, String lotNumber,
                       int quantity, LocalDate expiresAt, long unitCost) {}
