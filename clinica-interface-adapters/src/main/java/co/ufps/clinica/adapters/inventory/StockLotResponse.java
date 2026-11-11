package co.ufps.clinica.adapters.inventory;

import java.time.LocalDate;

public record StockLotResponse(String id, String branchId, String medicineId, String lotNumber,
                               int quantity, LocalDate expiresAt, long unitCost) {}