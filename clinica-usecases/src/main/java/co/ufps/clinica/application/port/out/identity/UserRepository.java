package co.ufps.clinica.application.port.out.identity;
import co.ufps.clinica.domain.identity.AccessAudit;
import co.ufps.clinica.domain.identity.PasswordReset;
import co.ufps.clinica.domain.identity.User;
import java.util.Optional;
public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    void savePasswordReset(PasswordReset reset);
    Optional<PasswordReset> findPasswordReset(String tokenHash);
    void saveAudit(AccessAudit audit);
}
