package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "branches", indexes = @Index(name = "idx_branch_clinic", columnList = "clinic_id"))
public class BranchJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;
    @Column(name = "name", length = 200, nullable = false)
    private String name;
    @Column(name = "address", length = 500)
    private String address;
    @Column(name = "phone", length = 40)
    private String phone;
    @Column(name = "time_zone", length = 60)
    private String timeZone;
    @Column(name = "active", nullable = false)
    private boolean active;

    public BranchJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
