package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.AppointmentRepository;
import co.ufps.clinica.domain.AppointmentStatus;
import java.time.Instant;
import java.util.*;

/** Agenda de citas. Conflictos y persistencia se resuelven mediante el puerto de repositorio. */
public final class AppointmentScheduler {
    private final AppointmentRepository repository;
    public AppointmentScheduler(AppointmentRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Appointment schedule(Appointment appointment) {
        if (appointment.startsAt().isBefore(Instant.now())) throw new IllegalArgumentException("No se puede agendar en el pasado");
        if (repository.hasConflict(appointment.doctorId(), appointment.officeId(), appointment.startsAt(), appointment.endsAt())) throw new IllegalStateException("El médico o consultorio ya está ocupado");
        return repository.save(appointment);
    }
    public Appointment changeStatus(UUID id, AppointmentStatus status) {
        Appointment current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        return repository.save(current.withStatus(status));
    }
}
