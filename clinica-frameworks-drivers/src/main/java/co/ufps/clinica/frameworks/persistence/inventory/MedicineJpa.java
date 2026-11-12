package co.ufps.clinica.frameworks.persistence.inventory;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "MEDICINES", indexes = @Index(name = "idx_medicine_name", columnList = "name"))
public class MedicineJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "name", length = 200, nullable = false)
    private String name;
    @Column(name = "presentation", length = 100)
    private String presentation;
    @Column(name = "dosage_form", length = 100)
    private String dosageForm;
    @Column(name = "unit", length = 40)
    private String unit;
    @Column(name = "active", nullable = false)
    private boolean active;

    public MedicineJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPresentation() { return presentation; }
    public void setPresentation(String presentation) { this.presentation = presentation; }
    public String getDosageForm() { return dosageForm; }
    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
