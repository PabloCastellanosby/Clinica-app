package co.ufps.clinica.frameworks.persistence.catalog;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "specialties")
public class SpecialtyJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "active", nullable = false)
    private boolean active;

    public SpecialtyJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
