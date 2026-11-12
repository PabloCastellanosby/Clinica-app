package co.ufps.clinica.domain.security;

public record Permission(PermissionId id, String key, String description) {
    public Permission {
        if (id == null || key == null || key.isBlank() || key.length() > 120
                || description == null || description.isBlank() || description.length() > 255)
            throw new IllegalArgumentException("El permiso requiere identificador, clave y descripción válidos");
    }
}
