package co.ufps.clinica.domain.identity;
import java.util.Optional;
public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    void savePasswordReset(PasswordReset reset);
    Optional<PasswordReset> findPasswordReset(String tokenHash);
    void saveAudit(AccessAudit audit);
}
