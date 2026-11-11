package co.ufps.clinica.frameworks.persistence.catalog;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "services", indexes = @Index(name = "idx_service_specialty", columnList = "specialty_id"))
public class ServiceJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "specialty_id")
    private UUID specialtyId;
    @Column(name = "name", length = 200, nullable = false)
    private String name;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "price_minor_units", nullable = false)
    private long priceMinorUnits;
    @Column(name = "currency", length = 10, nullable = false)
    private String currency;
    @Column(name = "active", nullable = false)
    private boolean active;

    public ServiceJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(UUID specialtyId) { this.specialtyId = specialtyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getPriceMinorUnits() { return priceMinorUnits; }
    public void setPriceMinorUnits(long priceMinorUnits) { this.priceMinorUnits = priceMinorUnits; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
