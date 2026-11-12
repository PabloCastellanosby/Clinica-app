package co.ufps.clinica.adapters.security;

import co.ufps.clinica.application.security.PermissionService;
import co.ufps.clinica.domain.identity.Role;
import java.util.List;
import java.util.Objects;

public final class PermissionController {
    private final PermissionService service;
    public PermissionController(PermissionService service) { this.service = Objects.requireNonNull(service); }

    public List<RolePermissionResponse> list(Role actorRole) {
        return service.list(actorRole).stream().map(grant -> new RolePermissionResponse(
                grant.role().name(), grant.permission().key(), grant.permission().description(), grant.allowed())).toList();
    }

    public void setAllowed(Role actorRole, String targetRole, String permissionKey, boolean allowed) {
        Role role;
        try { role = Role.valueOf(targetRole); }
        catch (RuntimeException exception) { throw new IllegalArgumentException("El rol indicado no existe"); }
        service.setAllowed(actorRole, role, permissionKey, allowed);
    }
}
