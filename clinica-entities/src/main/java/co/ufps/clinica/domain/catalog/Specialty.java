package co.ufps.clinica.domain.catalog;
public record Specialty(SpecialtyId id, String name, String description, boolean active) {
    public Specialty {
        if (id == null || name == null || name.isBlank())
            throw new IllegalArgumentException("La especialidad debe tener identificador y nombre");
    }
}
