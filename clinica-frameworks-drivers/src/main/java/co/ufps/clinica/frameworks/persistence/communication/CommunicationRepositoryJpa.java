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
        return jpa;
    }
    private TransactionalMessage toDomainMessage(TransactionalMessageJpa jpa) {
        return new TransactionalMessage(TransactionalMessageId.of(jpa.getId()),
                jpa.getRecipientId() == null ? null : UserId.of(jpa.getRecipientId()),
                jpa.getDestination(), jpa.getSubject(), jpa.getTemplate(),
                MessageStatus.valueOf(jpa.getStatus()), jpa.getCreatedAt(), jpa.getSentAt());
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
