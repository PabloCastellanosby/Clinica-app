package co.ufps.clinica.domain.clinic;
public record Office(OfficeId id, BranchId branchId, String name, String type, int capacity, boolean active) {
    public Office {
        if (id == null || branchId == null || name == null || name.isBlank() || capacity <= 0)
            throw new IllegalArgumentException("El consultorio debe tener sede, nombre y capacidad positiva");
    }
}
