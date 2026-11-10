package co.ufps.clinica.domain.inventory;
public record Medicine(MedicineId id, String name, String presentation, String dosageForm, String unit, boolean active) {}
