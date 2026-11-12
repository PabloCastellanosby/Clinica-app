package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ALLERGIES", indexes = @Index(name = "idx_allergy_patient", columnList = "patient_id"))
public class AllergyJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "patient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_allergies_patient"))
    private co.ufps.clinica.frameworks.persistence.patient.PatientJpa patientReference;
    @Column(name = "substance", length = 200, nullable = false)
    private String substance;
    @Column(name = "reaction", length = 500)
    private String reaction;
    @Column(name = "severity", length = 40)
    private String severity;

    public AllergyJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getSubstance() { return substance; }
    public void setSubstance(String substance) { this.substance = substance; }
    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
