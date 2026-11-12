package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "PRESCRIPTION_LINES")
public class PrescriptionLineJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "prescription_id", nullable = false)
    private UUID prescriptionId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "prescription_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_prescription_lines_prescription"))
    private co.ufps.clinica.frameworks.persistence.clinicalrecord.PrescriptionJpa prescriptionReference;
    @Column(name = "medicine", length = 200, nullable = false)
    private String medicine;
    @Column(name = "presentation", length = 100)
    private String presentation;
    @Column(name = "dose", length = 100)
    private String dose;
    @Column(name = "frequency", length = 100)
    private String frequency;
    @Column(name = "duration_days")
    private int durationDays;
    @Column(name = "quantity")
    private int quantity;

    public PrescriptionLineJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(UUID prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getMedicine() { return medicine; }
    public void setMedicine(String medicine) { this.medicine = medicine; }
    public String getPresentation() { return presentation; }
    public void setPresentation(String presentation) { this.presentation = presentation; }
    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
