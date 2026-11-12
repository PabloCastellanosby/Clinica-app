package co.ufps.clinica.frameworks.persistence.communication;

import co.ufps.clinica.domain.communication.*;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CommunicationRepositoryJpa implements CommunicationRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public TransactionalMessage saveMessage(TransactionalMessage message) {
        entityManager.merge(toJpa(message));
        return message;
    }

    @Override public Optional<TransactionalMessage> findById(TransactionalMessageId id) {
        TransactionalMessageJpa jpa = entityManager.find(TransactionalMessageJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainMessage);
    }

    @Override public List<TransactionalMessage> findByRecipient(UserId userId) {
        return entityManager.createQuery("SELECT m FROM TransactionalMessageJpa m WHERE m.recipientId = :uid", TransactionalMessageJpa.class)
                .setParameter("uid", userId.value()).getResultList().stream().map(this::toDomainMessage).toList();
    }

    @Override @Transactional public List<TransactionalMessage> claimPendingMessages(int limit) {
        Instant staleBefore = Instant.now().minusSeconds(600);
        List<TransactionalMessageJpa> pending = entityManager.createQuery(
                "SELECT m FROM TransactionalMessageJpa m WHERE m.attempts < 5 AND (m.status = :pending OR (m.status = :sending AND m.lastAttemptAt < :staleBefore)) ORDER BY m.createdAt",
                TransactionalMessageJpa.class)
                .setParameter("pending", MessageStatus.PENDING.name())
                .setParameter("sending", MessageStatus.SENDING.name())
                .setParameter("staleBefore", staleBefore)
                .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(Math.max(1, Math.min(limit, 200))).getResultList();
        for (TransactionalMessageJpa message : pending) {
            message.setStatus(MessageStatus.SENDING.name());
            message.setAttempts(message.getAttempts() + 1);
            message.setLastAttemptAt(Instant.now());
        }
        entityManager.flush();
        return pending.stream().map(this::toDomainMessage).toList();
    }

    @Override @Transactional public PatientNotice saveNotice(PatientNotice notice) {
        entityManager.merge(toJpa(notice));
        return notice;
    }

    @Override public Optional<PatientNotice> findByIdNotice(PatientNoticeId id) {
        PatientNoticeJpa jpa = entityManager.find(PatientNoticeJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainNotice);
    }

    @Override public List<PatientNotice> findActiveNotices(BranchId branchId) {
        return entityManager.createQuery("SELECT n FROM PatientNoticeJpa n WHERE n.branchId = :bid AND (n.expiresAt IS NULL OR n.expiresAt > :now)", PatientNoticeJpa.class)
                .setParameter("bid", branchId.value()).setParameter("now", Instant.now())
                .getResultList().stream().map(this::toDomainNotice).toList();
    }

    private TransactionalMessageJpa toJpa(TransactionalMessage m) {
        TransactionalMessageJpa jpa = new TransactionalMessageJpa();
        jpa.setId(m.id().value()); jpa.setRecipientId(m.recipientId() == null ? null : m.recipientId().value());
        jpa.setDestination(m.destination()); jpa.setSubject(m.subject());
        jpa.setTemplate(m.template()); jpa.setStatus(m.status().name());
        jpa.setCreatedAt(m.createdAt()); jpa.setSentAt(m.sentAt());
        jpa.setAttempts(m.attempts());
        jpa.setLastAttemptAt(m.lastAttemptAt());
        return jpa;
    }
    private TransactionalMessage toDomainMessage(TransactionalMessageJpa jpa) {
        return new TransactionalMessage(TransactionalMessageId.of(jpa.getId()),
                jpa.getRecipientId() == null ? null : UserId.of(jpa.getRecipientId()),
                jpa.getDestination(), jpa.getSubject(), jpa.getTemplate(),
                MessageStatus.valueOf(jpa.getStatus()), jpa.getCreatedAt(), jpa.getSentAt(), jpa.getAttempts(), jpa.getLastAttemptAt());
    }

    private PatientNoticeJpa toJpa(PatientNotice n) {
        PatientNoticeJpa jpa = new PatientNoticeJpa();
        jpa.setId(n.id().value()); jpa.setTitle(n.title()); jpa.setBody(n.body());
        jpa.setPublishedAt(n.publishedAt()); jpa.setExpiresAt(n.expiresAt());
        jpa.setBranchId(n.branchId() == null ? null : n.branchId().value());
        return jpa;
    }
    private PatientNotice toDomainNotice(PatientNoticeJpa jpa) {
        return new PatientNotice(PatientNoticeId.of(jpa.getId()), jpa.getTitle(), jpa.getBody(),
                jpa.getPublishedAt(), jpa.getExpiresAt(),
                jpa.getBranchId() == null ? null : BranchId.of(jpa.getBranchId()));
    }
}
