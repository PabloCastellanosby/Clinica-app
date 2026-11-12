package co.ufps.clinica.application.port.out.identity;
import co.ufps.clinica.domain.identity.AccessAudit;
import co.ufps.clinica.domain.identity.PasswordReset;
import co.ufps.clinica.domain.identity.User;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.identity.UserSession;
import co.ufps.clinica.domain.patient.Patient;
import java.time.Instant;
import java.util.Optional;
public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UserId id);
    User save(User user);
    User saveAndRevokeSessions(User user, Instant revokedAt);
    User registerPatientAccount(User user, Patient linkedPatient);
    void savePasswordReset(PasswordReset reset);
    Optional<PasswordReset> findPasswordReset(String tokenHash);
    void consumePasswordReset(PasswordReset reset, User updatedUser);
    void saveAudit(AccessAudit audit);
    UserSession saveSession(UserSession session);
    Optional<UserSession> findSessionByTokenHash(String tokenHash);
}
