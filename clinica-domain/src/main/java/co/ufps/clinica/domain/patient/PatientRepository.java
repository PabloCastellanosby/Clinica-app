package co.ufps.clinica.domain.patient;
import java.util.List;
import java.util.Optional;
public interface PatientRepository {
    Optional<Patient> findByDocument(String documentType, String documentNumber);
    Optional<Patient> findById(PatientId id);
    Patient save(Patient patient);
    List<Patient> search(String query);
}
