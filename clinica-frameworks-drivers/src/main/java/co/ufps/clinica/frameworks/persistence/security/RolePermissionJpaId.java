package co.ufps.clinica.frameworks.persistence.security;

import java.io.Serializable;
import java.util.Objects;

public class RolePermissionJpaId implements Serializable {
    private String role;
    private String permissionKey;

    public RolePermissionJpaId() {}
    public RolePermissionJpaId(String role, String permissionKey) {
        this.role = role;
        this.permissionKey = permissionKey;
    }
    @Override public boolean equals(Object other) {
        return other instanceof RolePermissionJpaId that
                && Objects.equals(role, that.role) && Objects.equals(permissionKey, that.permissionKey);
    }
    @Override public int hashCode() { return Objects.hash(role, permissionKey); }
}
