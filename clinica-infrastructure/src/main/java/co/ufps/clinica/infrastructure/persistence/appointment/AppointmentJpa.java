package co.ufps.clinica.infrastructure.persistence.appointment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_doctor_time", columnList = "doctor_id, starts_at, ends_at"),
        @Index(name = "idx_appointment_office_time", columnList = "office_id, starts_at, ends_at")
})
public class AppointmentJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @Column(name = "branch_id", nullable = false)
    private UUID branchId;
    @Column(name = "office_id", nullable = false)
    private UUID officeId;
    @Column(name = "service_id")
    private UUID serviceId;
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;
    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;
    @Column(name = "status", length = 30, nullable = false)
    private String status;
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AppointmentJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public UUID getOfficeId() { return officeId; }
    public void setOfficeId(UUID officeId) { this.officeId = officeId; }
    public UUID getServiceId() { return serviceId; }
    public void setServiceId(UUID serviceId) { this.serviceId = serviceId; }
    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }
    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
