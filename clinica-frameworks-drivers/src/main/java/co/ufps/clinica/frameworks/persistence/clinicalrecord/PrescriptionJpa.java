package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "prescriptions", indexes = @Index(name = "idx_prescription_encounter", columnList = "encounter_id"))
public class PrescriptionJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "encounter_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_prescriptions_encounter"))
    private co.ufps.clinica.frameworks.persistence.clinicalrecord.EncounterJpa encounterReference;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "patient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_prescriptions_patient"))
    private co.ufps.clinica.frameworks.persistence.patient.PatientJpa patientReference;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "doctor_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_prescriptions_doctor"))
    private co.ufps.clinica.frameworks.persistence.staff.DoctorJpa doctorReference;
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;
    @Column(name = "instructions", length = 2000)
    private String instructions;

    public PrescriptionJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEncounterId() { return encounterId; }
    public void setEncounterId(UUID encounterId) { this.encounterId = encounterId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
