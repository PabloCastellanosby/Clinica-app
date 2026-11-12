package co.ufps.clinica.frameworks.persistence.inventory;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "STOCK_MOVEMENTS")
public class StockMovementJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "lot_id", nullable = false)
    private UUID lotId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "lot_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_stock_movements_lot"))
    private co.ufps.clinica.frameworks.persistence.inventory.StockLotJpa lotReference;
    @Column(name = "type", length = 20, nullable = false)
    private String type;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "reason", length = 500)
    private String reason;
    @Column(name = "actor_id", nullable = false)
    private UUID actorId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "actor_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_stock_movements_actor"))
    private co.ufps.clinica.frameworks.persistence.identity.UserJpa actorReference;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public StockMovementJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLotId() { return lotId; }
    public void setLotId(UUID lotId) { this.lotId = lotId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public UUID getActorId() { return actorId; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
