package co.ufps.clinica.adapters.inventory;

import java.time.LocalDate;

public record CreateStockLotRequest(String branchId, String medicineId, String lotNumber,
                                    int quantity, LocalDate expiresAt, long unitCost) {}