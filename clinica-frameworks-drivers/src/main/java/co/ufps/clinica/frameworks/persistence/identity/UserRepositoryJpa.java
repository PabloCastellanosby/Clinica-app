package co.ufps.clinica.frameworks.persistence.identity;

import co.ufps.clinica.domain.identity.*;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.frameworks.persistence.patient.PatientRepositoryJpa;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

/** Adaptador JPA del repositorio de identidad. */
@ApplicationScoped
public class UserRepositoryJpa implements UserRepository {
    @PersistenceContext EntityManager entityManager;
    @Inject PatientRepositoryJpa patients;

    @Override public Optional<User> findByEmail(String email) {
        return entityManager.createQuery("SELECT u FROM UserJpa u WHERE u.email = :email", UserJpa.class)
                .setParameter("email", email).getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override public Optional<User> findById(UserId id) {
        return Optional.ofNullable(entityManager.find(UserJpa.class, id.value())).map(this::toDomain);
    }

    @Override @Transactional public User save(User user) {
        entityManager.merge(toJpa(user));
        return user;
    }

    @Override @Transactional public User saveAndRevokeSessions(User user, Instant revokedAt) {
        entityManager.merge(toJpa(user));
        entityManager.createQuery("UPDATE UserSessionJpa s SET s.revokedAt = :now WHERE s.userId = :userId AND s.revokedAt IS NULL")
                .setParameter("now", revokedAt).setParameter("userId", user.id().value()).executeUpdate();
        return user;
    }

    @Override @Transactional public User registerPatientAccount(User user, Patient linkedPatient) {
        entityManager.persist(toJpa(user));
        patients.save(linkedPatient);
        return user;
    }

    @Override @Transactional public void savePasswordReset(PasswordReset reset) {
        entityManager.merge(toJpa(reset));
    }

    @Override public Optional<PasswordReset> findPasswordReset(String tokenHash) {
        return entityManager.createQuery("SELECT r FROM PasswordResetJpa r WHERE r.tokenHash = :tokenHash", PasswordResetJpa.class)
                .setParameter("tokenHash", tokenHash).getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override @Transactional public void consumePasswordReset(PasswordReset reset, User updatedUser) {
        int consumed = entityManager.createQuery("UPDATE PasswordResetJpa r SET r.usedAt = :now WHERE r.id = :id AND r.usedAt IS NULL AND r.expiresAt > :now")
                .setParameter("now", reset.usedAt()).setParameter("id", reset.id().value()).executeUpdate();
        if (consumed != 1) throw new IllegalArgumentException("El enlace de recuperación ya fue usado o venció");
        entityManager.createQuery("UPDATE PasswordResetJpa r SET r.usedAt = :now WHERE r.userId = :userId AND r.usedAt IS NULL")
                .setParameter("now", reset.usedAt()).setParameter("userId", updatedUser.id().value()).executeUpdate();
        entityManager.merge(toJpa(updatedUser));
        entityManager.createQuery("UPDATE UserSessionJpa s SET s.revokedAt = :now WHERE s.userId = :userId AND s.revokedAt IS NULL")
                .setParameter("now", reset.usedAt()).setParameter("userId", updatedUser.id().value()).executeUpdate();
    }

    @Override @Transactional public void saveAudit(AccessAudit audit) {
        entityManager.persist(toJpa(audit));
    }

    @Override @Transactional public UserSession saveSession(UserSession session) {
        UserSessionJpa jpa = new UserSessionJpa();
        jpa.setId(session.id().value());
        jpa.setUserId(session.userId().value());
        jpa.setTokenHash(session.tokenHash());
        jpa.setCreatedAt(session.createdAt());
        jpa.setExpiresAt(session.expiresAt());
        jpa.setRevokedAt(session.revokedAt());
        entityManager.merge(jpa);
        return session;
    }

    @Override public Optional<UserSession> findSessionByTokenHash(String tokenHash) {
        return entityManager.createQuery("SELECT s FROM UserSessionJpa s WHERE s.tokenHash = :tokenHash", UserSessionJpa.class)
                .setParameter("tokenHash", tokenHash).getResultStream().findFirst()
                .map(s -> new UserSession(UserSessionId.of(s.getId()), UserId.of(s.getUserId()), s.getTokenHash(),
                        s.getCreatedAt(), s.getExpiresAt(), s.getRevokedAt()));
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
