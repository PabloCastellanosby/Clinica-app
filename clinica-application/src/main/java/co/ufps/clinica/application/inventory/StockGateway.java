package co.ufps.clinica.application.inventory;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.inventory.StockLot;
import co.ufps.clinica.domain.inventory.StockMovement;
import java.time.LocalDate;
import java.util.List;
public interface StockGateway {
    List<StockLot> belowMinimum(BranchId branchId);
    List<StockLot> expiringBefore(BranchId branchId, LocalDate date);
    StockLot adjust(StockMovement movement);
}
