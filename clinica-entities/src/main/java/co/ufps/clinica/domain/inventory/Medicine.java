package co.ufps.clinica.domain.inventory;
public record Medicine(MedicineId id, String name, String presentation, String dosageForm, String unit, boolean active) {
    public Medicine {
        if (id == null || name == null || name.isBlank())
            throw new IllegalArgumentException("El medicamento debe tener identificador y nombre");
    }
}
