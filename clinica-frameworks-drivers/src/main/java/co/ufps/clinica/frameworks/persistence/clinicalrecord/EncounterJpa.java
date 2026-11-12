package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ENCOUNTERS", indexes = @Index(name = "idx_encounter_patient", columnList = "patient_id"),
       uniqueConstraints = @UniqueConstraint(name = "uq_encounter_appointment", columnNames = "appointment_id"))
public class EncounterJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "appointment_id", nullable = false)
    private UUID appointmentId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "appointment_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_encounters_appointment"))
    private co.ufps.clinica.frameworks.persistence.appointment.AppointmentJpa appointmentReference;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "patient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_encounters_patient"))
    private co.ufps.clinica.frameworks.persistence.patient.PatientJpa patientReference;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "doctor_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_encounters_doctor"))
    private co.ufps.clinica.frameworks.persistence.staff.DoctorJpa doctorReference;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @Column(name = "reason", length = 500)
    private String reason;
    @Column(name = "diagnosis", length = 2000)
    private String diagnosis;
    @Column(name = "notes", length = 5000)
    private String notes;
    @Column(name = "patient_visible", nullable = false)
    private boolean patientVisible;

    public EncounterJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isPatientVisible() { return patientVisible; }
    public void setPatientVisible(boolean patientVisible) { this.patientVisible = patientVisible; }
}
