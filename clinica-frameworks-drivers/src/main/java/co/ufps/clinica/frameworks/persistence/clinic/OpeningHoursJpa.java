package co.ufps.clinica.frameworks.persistence.clinic;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "OPENING_HOURS", indexes = @Index(name = "idx_opening_hours_branch_day", columnList = "branch_id, day_of_week"))
public class OpeningHoursJpa {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "branch_id", nullable = false) private UUID branchId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_opening_hours_branch"))
    private co.ufps.clinica.frameworks.persistence.clinic.BranchJpa branchReference;
    @Column(name = "day_of_week", length = 20, nullable = false) private String day;
    @Column(name = "opens_at", nullable = false) private LocalTime opensAt;
    @Column(name = "closes_at", nullable = false) private LocalTime closesAt;
    public OpeningHoursJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public LocalTime getOpensAt() { return opensAt; }
    public void setOpensAt(LocalTime opensAt) { this.opensAt = opensAt; }
    public LocalTime getClosesAt() { return closesAt; }
    public void setClosesAt(LocalTime closesAt) { this.closesAt = closesAt; }
}
