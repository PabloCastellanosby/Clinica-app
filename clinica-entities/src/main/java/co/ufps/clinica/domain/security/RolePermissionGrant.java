package co.ufps.clinica.domain.security;

import co.ufps.clinica.domain.identity.Role;

public record RolePermissionGrant(Role role, Permission permission, boolean allowed) {
    public RolePermissionGrant {
        if (role == null || permission == null)
            throw new IllegalArgumentException("La asignación requiere rol y permiso");
    }
}
