package co.ufps.clinica.frameworks.persistence.billing;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "INVOICE_LINES")
public class InvoiceLineJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "invoice_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_invoice_lines_invoice"))
    private co.ufps.clinica.frameworks.persistence.billing.InvoiceJpa invoiceReference;
    @Column(name = "description", length = 500, nullable = false)
    private String description;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "unit_price", nullable = false)
    private long unitPrice;
    @Column(name = "total", nullable = false)
    private long total;

    public InvoiceLineJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getInvoiceId() { return invoiceId; }
    public void setInvoiceId(UUID invoiceId) { this.invoiceId = invoiceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getUnitPrice() { return unitPrice; }
    public void setUnitPrice(long unitPrice) { this.unitPrice = unitPrice; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
