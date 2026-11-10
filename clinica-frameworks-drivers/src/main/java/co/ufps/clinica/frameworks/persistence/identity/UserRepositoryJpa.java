package co.ufps.clinica.frameworks.persistence.identity;

import co.ufps.clinica.domain.identity.*;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

/** Adaptador JPA del repositorio de identidad. */
@ApplicationScoped
public class UserRepositoryJpa implements UserRepository {
    @PersistenceContext EntityManager entityManager;

    @Override public Optional<User> findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM UserJpa u WHERE u.email = :email", UserJpa.class)
                .setParameter("email", email).getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override @Transactional public User save(User user) {
        entityManager.merge(toJpa(user));
        return user;
    }

    @Override @Transactional public void savePasswordReset(PasswordReset reset) {
        entityManager.merge(toJpa(reset));
    }

    @Override public Optional<PasswordReset> findPasswordReset(String tokenHash) {
        return entityManager.createQuery("SELECT r FROM PasswordResetJpa r WHERE r.tokenHash = :tokenHash", PasswordResetJpa.class)
                .setParameter("tokenHash", tokenHash).getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override @Transactional public void saveAudit(AccessAudit audit) {
        entityManager.persist(toJpa(audit));
    }

    private UserJpa toJpa(User user) {
        UserJpa jpa = new UserJpa();
        jpa.setId(user.id().value()); jpa.setEmail(user.email()); jpa.setPasswordHash(user.passwordHash());
        jpa.setRole(user.role().name()); jpa.setActive(user.active()); jpa.setCreatedAt(user.createdAt());
        jpa.setLastLoginAt(user.lastLoginAt());
        return jpa;
    }
    private User toDomain(UserJpa jpa) {
        return new User(UserId.of(jpa.getId()), jpa.getEmail(), jpa.getPasswordHash(), Role.valueOf(jpa.getRole()),
                jpa.isActive(), jpa.getCreatedAt(), jpa.getLastLoginAt());
    }
    private PasswordResetJpa toJpa(PasswordReset reset) {
        PasswordResetJpa jpa = new PasswordResetJpa();
        jpa.setId(reset.id().value()); jpa.setUserId(reset.userId().value()); jpa.setTokenHash(reset.tokenHash());
        jpa.setExpiresAt(reset.expiresAt()); jpa.setUsedAt(reset.usedAt());
        return jpa;
    }
    private PasswordReset toDomain(PasswordResetJpa jpa) {
        return new PasswordReset(PasswordResetId.of(jpa.getId()), UserId.of(jpa.getUserId()), jpa.getTokenHash(),
                jpa.getExpiresAt(), jpa.getUsedAt());
    }
    private AccessAuditJpa toJpa(AccessAudit audit) {
        AccessAuditJpa jpa = new AccessAuditJpa();
        jpa.setId(audit.id().value()); jpa.setUserId(audit.userId() == null ? null : audit.userId().value());
        jpa.setAction(audit.action()); jpa.setIpAddress(audit.ipAddress()); jpa.setOccurredAt(audit.occurredAt());
        jpa.setSuccessful(audit.successful());
        return jpa;
    }
}
