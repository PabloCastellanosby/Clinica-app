package co.ufps.clinica.frameworks.persistence.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "permissions")
public class PermissionJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "permission_key", nullable = false, unique = true, length = 120)
    private String permissionKey;
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    public PermissionJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
