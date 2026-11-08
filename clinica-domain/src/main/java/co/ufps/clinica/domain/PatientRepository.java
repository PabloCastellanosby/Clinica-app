package co.ufps.clinica.domain;

import java.util.*;

/** Puerto de persistencia para pacientes. */
public interface PatientRepository {
    Optional<ClinicModel.Patient> findByDocument(String documentType, String documentNumber);
    Optional<ClinicModel.Patient> findById(PatientId id);
    ClinicModel.Patient save(ClinicModel.Patient patient);
    List<ClinicModel.Patient> search(String query);
}
