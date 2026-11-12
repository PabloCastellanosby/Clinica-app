package co.ufps.clinica.frameworks.persistence.staff;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "DOCTOR_AVAILABILITY")
public class DoctorAvailabilityJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "doctor_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_doctor_availability_doctor"))
    private co.ufps.clinica.frameworks.persistence.staff.DoctorJpa doctorReference;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "branch_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_doctor_availability_branch"))
    private co.ufps.clinica.frameworks.persistence.clinic.BranchJpa branchReference;
    @Column(name = "day_of_week", length = 20, nullable = false)
    private String day;
    @Column(name = "starts_at", nullable = false)
    private LocalTime startsAt;
    @Column(name = "ends_at", nullable = false)
    private LocalTime endsAt;
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    @Column(name = "valid_until")
    private LocalDate validUntil;

    public DoctorAvailabilityJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public LocalTime getStartsAt() { return startsAt; }
    public void setStartsAt(LocalTime startsAt) { this.startsAt = startsAt; }
    public LocalTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalTime endsAt) { this.endsAt = endsAt; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
}
