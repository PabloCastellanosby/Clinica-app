package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "holidays", indexes = @Index(name = "idx_holiday_branch", columnList = "branch_id"),
       uniqueConstraints = @UniqueConstraint(name = "uq_holiday_branch_date", columnNames = {"branch_id", "date"}))
public class HolidayJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_holidays_branch"))
    private co.ufps.clinica.frameworks.persistence.clinic.BranchJpa branchReference;
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
