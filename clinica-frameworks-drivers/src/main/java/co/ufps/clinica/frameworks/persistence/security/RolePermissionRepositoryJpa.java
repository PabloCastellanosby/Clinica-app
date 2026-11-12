package co.ufps.clinica.frameworks.persistence.security;

import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.security.Permission;
import co.ufps.clinica.domain.security.PermissionId;
import co.ufps.clinica.domain.security.RolePermissionGrant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class RolePermissionRepositoryJpa implements RolePermissionRepository {
    @PersistenceContext EntityManager entityManager;

    @Override public boolean isAllowed(Role role, String permissionKey) {
        if (role == null || permissionKey == null) return false;
        Long count = entityManager.createQuery("SELECT COUNT(r) FROM RolePermissionJpa r WHERE r.role = :role AND r.permissionKey = :key AND r.allowed = true", Long.class)
                .setParameter("role", role.name()).setParameter("key", permissionKey).getSingleResult();
        return count > 0;
    }

    @Override public List<RolePermissionGrant> findAllGrants() {
        return entityManager.createQuery("SELECT r, p FROM RolePermissionJpa r, PermissionJpa p WHERE r.permissionKey = p.permissionKey ORDER BY r.permissionKey, r.role", Object[].class)
                .getResultList().stream().map(row -> {
                    RolePermissionJpa rolePermission = (RolePermissionJpa) row[0];
                    return new RolePermissionGrant(Role.valueOf(rolePermission.getRole()),
                            toDomain((PermissionJpa) row[1]), rolePermission.isAllowed());
                }).toList();
    }

    @Override @Transactional public void ensureDefault(Permission permission, Role role, boolean allowed) {
        PermissionJpa permissionJpa = findPermission(permission.key());
        if (permissionJpa == null) {
            permissionJpa = new PermissionJpa();
            permissionJpa.setId(permission.id().value());
            permissionJpa.setPermissionKey(permission.key());
            permissionJpa.setDescription(permission.description());
            entityManager.persist(permissionJpa);
        }
        RolePermissionJpaId id = new RolePermissionJpaId(role.name(), permission.key());
        if (entityManager.find(RolePermissionJpa.class, id) == null) {
            RolePermissionJpa grant = new RolePermissionJpa();
            grant.setRole(role.name());
            grant.setPermissionKey(permission.key());
            grant.setAllowed(allowed);
            entityManager.persist(grant);
        }
    }

    @Override @Transactional public void setAllowed(Role role, String permissionKey, boolean allowed) {
        if (findPermission(permissionKey) == null) throw new java.util.NoSuchElementException("Permiso no encontrado");
        RolePermissionJpa grant = entityManager.find(RolePermissionJpa.class,
                new RolePermissionJpaId(role.name(), permissionKey));
        if (grant == null) throw new java.util.NoSuchElementException("Permiso no configurado para el rol");
        grant.setAllowed(allowed);
    }

    private PermissionJpa findPermission(String permissionKey) {
        List<PermissionJpa> found = entityManager.createQuery("SELECT p FROM PermissionJpa p WHERE p.permissionKey = :key", PermissionJpa.class)
                .setParameter("key", permissionKey).setMaxResults(1).getResultList();
        return found.isEmpty() ? null : found.get(0);
    }

    private Permission toDomain(PermissionJpa jpa) {
        return new Permission(PermissionId.of(jpa.getId()), jpa.getPermissionKey(), jpa.getDescription());
    }
}
