package co.ufps.clinica.api.composition;

import co.ufps.clinica.adapters.patient.PatientController;
import co.ufps.clinica.application.patient.PatientRegistry;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/** Punto externo de composición: conecta adaptador JPA, caso de uso y controlador. */
@ApplicationScoped
public class CleanArchitectureComposition {
    @Produces @Dependent PatientRegistry patientRegistry(PatientRepository repository) { return new PatientRegistry(repository); }
    @Produces @Dependent PatientController patientController(PatientRegistry patientRegistry) { return new PatientController(patientRegistry); }
}
