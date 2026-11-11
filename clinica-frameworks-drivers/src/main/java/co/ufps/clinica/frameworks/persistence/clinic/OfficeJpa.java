package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "offices", indexes = @Index(name = "idx_office_branch", columnList = "branch_id"))
public class OfficeJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "type", length = 50)
    private String type;
    @Column(name = "capacity")
    private int capacity;
    @Column(name = "active", nullable = false)
    private boolean active;

    public OfficeJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
