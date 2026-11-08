package co.ufps.clinica.domain;

import java.util.*;

/** Puerto para credenciales, recuperación y auditoría de acceso. */
public interface UserRepository {
    Optional<ClinicModel.User> findByEmail(String email);
    ClinicModel.User save(ClinicModel.User user);
    void savePasswordReset(ClinicModel.PasswordReset reset);
    Optional<ClinicModel.PasswordReset> findPasswordReset(String tokenHash);
    void saveAudit(ClinicModel.AccessAudit audit);
}
