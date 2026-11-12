package co.ufps.clinica.frameworks.persistence.clinicalrecord;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vital_signs")
public class VitalSignsJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "encounter_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_vital_signs_encounter"))
    private co.ufps.clinica.frameworks.persistence.clinicalrecord.EncounterJpa encounterReference;
    @Column(name = "temperature_c", precision = 4, scale = 1)
    private BigDecimal temperatureC;
    @Column(name = "systolic")
    private Integer systolic;
    @Column(name = "diastolic")
    private Integer diastolic;
    @Column(name = "heart_rate")
    private Integer heartRate;
    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;
    @Column(name = "weight_kg", precision = 6, scale = 2)
    private BigDecimal weightKg;
    @Column(name = "height_cm", precision = 5, scale = 1)
    private BigDecimal heightCm;
    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt;

    public VitalSignsJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEncounterId() { return encounterId; }
    public void setEncounterId(UUID encounterId) { this.encounterId = encounterId; }
    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }
    public Integer getSystolic() { return systolic; }
    public void setSystolic(Integer systolic) { this.systolic = systolic; }
    public Integer getDiastolic() { return diastolic; }
    public void setDiastolic(Integer diastolic) { this.diastolic = diastolic; }
    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
    public Integer getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public Instant getMeasuredAt() { return measuredAt; }
    public void setMeasuredAt(Instant measuredAt) { this.measuredAt = measuredAt; }
}
