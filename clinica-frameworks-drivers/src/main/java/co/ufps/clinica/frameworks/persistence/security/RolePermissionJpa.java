package co.ufps.clinica.frameworks.persistence.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_permissions")
@IdClass(RolePermissionJpaId.class)
public class RolePermissionJpa {
    @Id @Column(name = "role_name", nullable = false, length = 40)
    private String role;
    @Id @Column(name = "permission_key", nullable = false, length = 120)
    private String permissionKey;
    @Column(name = "allowed", nullable = false)
    private boolean allowed;

    public RolePermissionJpa() {}
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }
    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }
}
