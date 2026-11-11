package co.ufps.clinica.application.inventory;

public record RegisterMedicineCommand(String name, String presentation, String dosageForm, String unit) {}
