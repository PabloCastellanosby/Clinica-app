package co.ufps.clinica.application.security;

import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.security.RolePermissionGrant;
import java.util.List;
import java.util.Objects;

public final class PermissionService {
    private final RolePermissionRepository repository;

    public PermissionService(RolePermissionRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public List<RolePermissionGrant> list(Role actorRole) {
        requireAdministrator(actorRole);
        return repository.findAllGrants();
    }

    public void setAllowed(Role actorRole, Role targetRole, String permissionKey, boolean allowed) {
        requireAdministrator(actorRole);
        if (targetRole == null || targetRole == Role.ADMINISTRADOR)
            throw new IllegalArgumentException("Los permisos del administrador no se pueden modificar");
        if (permissionKey == null || permissionKey.isBlank())
            throw new IllegalArgumentException("La clave del permiso es obligatoria");
        repository.setAllowed(targetRole, permissionKey, allowed);
    }

    private static void requireAdministrator(Role role) {
        if (role != Role.ADMINISTRADOR) throw new SecurityException("Solo un administrador puede gestionar permisos");
    }
}
