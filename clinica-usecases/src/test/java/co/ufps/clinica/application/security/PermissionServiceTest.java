package co.ufps.clinica.application.security;

import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.security.Permission;
import co.ufps.clinica.domain.security.RolePermissionGrant;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PermissionServiceTest {
    @Test
    void administratorCanChangePermissionForStaffRole() {
        InMemoryPermissions repository = new InMemoryPermissions();
        PermissionService service = new PermissionService(repository);

        service.setAllowed(Role.ADMINISTRADOR, Role.MEDICO, "ClinicalRecordResource.getEncounter", false);

        assertEquals(Role.MEDICO, repository.lastRole);
        assertEquals("ClinicalRecordResource.getEncounter", repository.lastPermission);
        assertEquals(false, repository.lastAllowed);
    }

    @Test
    void nonAdministratorCannotChangePermissions() {
        PermissionService service = new PermissionService(new InMemoryPermissions());

        assertThrows(SecurityException.class, () -> service.setAllowed(
                Role.RECEPCION, Role.MEDICO, "PatientResource.findById", true));
    }

    @Test
    void administratorPermissionsCannotBeModified() {
        PermissionService service = new PermissionService(new InMemoryPermissions());

        assertThrows(IllegalArgumentException.class, () -> service.setAllowed(
                Role.ADMINISTRADOR, Role.ADMINISTRADOR, "IdentityResource.provisionStaff", false));
    }

    private static final class InMemoryPermissions implements RolePermissionRepository {
        private Role lastRole;
        private String lastPermission;
        private boolean lastAllowed;
        @Override public boolean isAllowed(Role role, String permissionKey) { return false; }
        @Override public List<RolePermissionGrant> findAllGrants() { return List.of(); }
        @Override public void ensureDefault(Permission permission, Role role, boolean allowed) {}
        @Override public void setAllowed(Role role, String permissionKey, boolean allowed) {
            lastRole = role;
            lastPermission = permissionKey;
            lastAllowed = allowed;
        }
    }
}
