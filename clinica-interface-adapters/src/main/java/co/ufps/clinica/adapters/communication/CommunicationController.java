package co.ufps.clinica.adapters.communication;

import co.ufps.clinica.application.communication.*;
import co.ufps.clinica.domain.communication.*;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinic.BranchId;
import java.util.List;
import java.util.Objects;

public final class CommunicationController {
    private final CommunicationService service;
    public CommunicationController(CommunicationService service) { this.service = Objects.requireNonNull(service); }

    public MessageResponse sendMessage(SendMessageRequest request) {
        TransactionalMessage m = service.sendMessage(new SendMessageCommand(
                UserId.of(request.recipientId()), request.destination(), request.subject(), request.template()));
        return presentMessage(m);
    }

    public NoticeResponse publishNotice(PublishNoticeRequest request) {
        PatientNotice n = service.publishNotice(new PublishNoticeCommand(
                request.title(), request.body(), request.expiresAt(), BranchId.of(request.branchId())));
        return presentNotice(n);
    }

    public List<NoticeResponse> getActiveNotices(String branchId) {
        return service.getActiveNotices(BranchId.of(branchId)).stream().map(this::presentNotice).toList();
    }

    public List<MessageResponse> getMessagesForUser(String userId) {
        return service.getMessagesForUser(UserId.of(userId)).stream().map(this::presentMessage).toList();
    }

    private MessageResponse presentMessage(TransactionalMessage m) {
        return new MessageResponse(m.id().value().toString(), m.recipientId() != null ? m.recipientId().value().toString() : null,
                m.destination(), m.subject(), m.template(), m.status().name(), m.createdAt(), m.sentAt());
    }

    private NoticeResponse presentNotice(PatientNotice n) {
        return new NoticeResponse(n.id().value().toString(), n.title(), n.body(),
                n.publishedAt(), n.expiresAt(), n.branchId().value().toString());
    }
}