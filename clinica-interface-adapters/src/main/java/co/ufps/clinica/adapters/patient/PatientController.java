package co.ufps.clinica.adapters.patient;

import co.ufps.clinica.application.patient.PatientRegistry;
import co.ufps.clinica.application.patient.RegisterPatientCommand;
import co.ufps.clinica.domain.patient.Patient;
import java.util.List;
import java.util.Objects;

/** Adapta modelos de transporte al caso de uso y presenta respuestas sin exponer la entidad. */
public final class PatientController {
    private final PatientRegistry patientRegistry;
    public PatientController(PatientRegistry patientRegistry) { this.patientRegistry = Objects.requireNonNull(patientRegistry); }
    public PatientResponse register(RegisterPatientRequest request) {
        Patient patient = patientRegistry.register(new RegisterPatientCommand(request.documentType(), request.documentNumber(),
                request.firstName(), request.lastName(), request.birthDate(), request.email(), request.phone(),
                request.address(), request.affiliationType()));
        return present(patient);
    }
    public List<PatientResponse> search(String query) { return patientRegistry.search(query).stream().map(this::present).toList(); }
    private PatientResponse present(Patient patient) {
        return new PatientResponse(patient.id().value().toString(), patient.documentType(), patient.documentNumber(),
                patient.firstName(), patient.lastName(), patient.birthDate(), patient.email(), patient.phone(),
                patient.address(), patient.affiliationType());
    }
}
