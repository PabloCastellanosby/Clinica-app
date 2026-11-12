package co.ufps.clinica.application.port.out.security;

import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.security.Permission;
import co.ufps.clinica.domain.security.RolePermissionGrant;
import java.util.List;

public interface RolePermissionRepository {
    boolean isAllowed(Role role, String permissionKey);
    List<RolePermissionGrant> findAllGrants();
    void ensureDefault(Permission permission, Role role, boolean allowed);
    default void ensureDefaults(List<RolePermissionGrant> defaults) {
        for (RolePermissionGrant grant : defaults)
            ensureDefault(grant.permission(), grant.role(), grant.allowed());
    }
    void setAllowed(Role role, String permissionKey, boolean allowed);
}
