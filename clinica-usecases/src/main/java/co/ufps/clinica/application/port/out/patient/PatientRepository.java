package co.ufps.clinica.application.port.out.patient;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.identity.UserId;
import java.util.List;
import java.util.Optional;
public interface PatientRepository {
    Optional<Patient> findByDocument(String documentType, String documentNumber);
    Optional<Patient> findById(PatientId id);
    Optional<Patient> findByUserId(UserId userId);
    Patient save(Patient patient);
    List<Patient> search(String query);
}
