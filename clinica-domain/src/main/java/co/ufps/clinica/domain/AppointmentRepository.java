package co.ufps.clinica.domain;

import java.time.Instant;
import java.util.*;

/** Puerto de persistencia para agenda y detección de cruces de horario. */
public interface AppointmentRepository {
    boolean hasConflict(UUID doctorId, UUID officeId, Instant from, Instant until);
    ClinicModel.Appointment save(ClinicModel.Appointment appointment);
    Optional<ClinicModel.Appointment> findById(UUID id);
}
