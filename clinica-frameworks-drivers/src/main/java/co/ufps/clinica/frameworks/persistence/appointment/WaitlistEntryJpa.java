package co.ufps.clinica.frameworks.persistence.appointment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "WAITLIST_ENTRIES", indexes = {
        @Index(name = "idx_waitlist_patient", columnList = "patient_id, status"),
        @Index(name = "idx_waitlist_specialty", columnList = "specialty_id, status")
})
public class WaitlistEntryJpa {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "patient_id", nullable = false) private UUID patientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "patient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_waitlist_entries_patient"))
    private co.ufps.clinica.frameworks.persistence.patient.PatientJpa patientReference;
    @Column(name = "doctor_id") private UUID doctorId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "doctor_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_waitlist_entries_doctor"))
    private co.ufps.clinica.frameworks.persistence.staff.DoctorJpa doctorReference;
    @Column(name = "specialty_id") private UUID specialtyId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "specialty_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_waitlist_entries_specialty"))
    private co.ufps.clinica.frameworks.persistence.catalog.SpecialtyJpa specialtyReference;
    @Column(name = "requested_from", nullable = false) private Instant requestedFrom;
    @Column(name = "requested_until", nullable = false) private Instant requestedUntil;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "status", length = 20, nullable = false) private String status;
    public WaitlistEntryJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public UUID getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(UUID specialtyId) { this.specialtyId = specialtyId; }
    public Instant getRequestedFrom() { return requestedFrom; }
    public void setRequestedFrom(Instant requestedFrom) { this.requestedFrom = requestedFrom; }
    public Instant getRequestedUntil() { return requestedUntil; }
    public void setRequestedUntil(Instant requestedUntil) { this.requestedUntil = requestedUntil; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
