package co.ufps.clinica.frameworks.persistence.inventory;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stock_lots", indexes = {
        @Index(name = "idx_stock_lot_branch", columnList = "branch_id"),
        @Index(name = "idx_stock_lot_medicine", columnList = "medicine_id")
})
public class StockLotJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @Column(name = "medicine_id", nullable = false)
    private UUID medicineId;
    @Column(name = "lot_number", length = 100, nullable = false)
    private String lotNumber;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "expires_at")
    private LocalDate expiresAt;
    @Column(name = "unit_cost", nullable = false)
    private long unitCost;

    public StockLotJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }
    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
    public long getUnitCost() { return unitCost; }
    public void setUnitCost(long unitCost) { this.unitCost = unitCost; }
}
