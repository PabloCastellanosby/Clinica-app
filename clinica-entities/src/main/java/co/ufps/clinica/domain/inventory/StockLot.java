package co.ufps.clinica.domain.inventory;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.LocalDate;
public record StockLot(StockLotId id, BranchId branchId, MedicineId medicineId, String lotNumber,
                       int quantity, LocalDate expiresAt, long unitCost) {
    public StockLot {
        if (id == null || branchId == null || medicineId == null || lotNumber == null || lotNumber.isBlank()
                || quantity < 0 || unitCost < 0)
            throw new IllegalArgumentException("El lote debe tener sede, medicamento, número, cantidades y costos válidos");
    }
}
