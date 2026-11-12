package co.ufps.clinica.domain.clinic;
public record Clinic(ClinicId id, String name, String taxId, String email, String phone, boolean active) {
    public Clinic {
        if (id == null || name == null || name.isBlank())
            throw new IllegalArgumentException("La clínica debe tener identificador y nombre");
    }
}
