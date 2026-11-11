package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "holidays", indexes = @Index(name = "idx_holiday_branch", columnList = "branch_id"))
public class HolidayJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "description", length = 500)
    private String description;

    public HolidayJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
