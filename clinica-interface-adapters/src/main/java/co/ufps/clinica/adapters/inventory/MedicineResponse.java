package co.ufps.clinica.adapters.inventory;

public record MedicineResponse(String id, String name, String presentation, String dosageForm, String unit, boolean active) {}