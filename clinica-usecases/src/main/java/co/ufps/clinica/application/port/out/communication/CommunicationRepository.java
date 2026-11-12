package co.ufps.clinica.application.port.out.communication;

import co.ufps.clinica.domain.communication.*;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinic.BranchId;
import java.util.List;
import java.util.Optional;

public interface CommunicationRepository {
    TransactionalMessage saveMessage(TransactionalMessage message);
    Optional<TransactionalMessage> findById(TransactionalMessageId id);
    List<TransactionalMessage> findByRecipient(UserId userId);
    List<TransactionalMessage> claimPendingMessages(int limit);
    PatientNotice saveNotice(PatientNotice notice);
    Optional<PatientNotice> findByIdNotice(PatientNoticeId id);
    List<PatientNotice> findActiveNotices(BranchId branchId);
}
