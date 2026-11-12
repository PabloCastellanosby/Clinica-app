package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "CLINICS")
public class ClinicJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "name", length = 200, nullable = false)
    private String name;
    @Column(name = "tax_id", length = 50)
    private String taxId;
    @Column(name = "email", length = 254)
    private String email;
    @Column(name = "phone", length = 40)
    private String phone;
    @Column(name = "active", nullable = false)
    private boolean active;

    public ClinicJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
