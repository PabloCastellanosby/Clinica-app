package co.ufps.clinica.application.inventory;

import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.inventory.MedicineId;
import java.time.LocalDate;

public record CreateStockLotCommand(BranchId branchId, MedicineId medicineId,
                                    String lotNumber, int quantity, LocalDate expiresAt, long unitCost) {}
