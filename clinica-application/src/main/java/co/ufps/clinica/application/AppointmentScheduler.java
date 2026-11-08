package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.AppointmentRepository;
import co.ufps.clinica.domain.AppointmentStatus;
import co.ufps.clinica.domain.AppointmentId;
import java.time.Instant;
import java.util.*;

/** Agenda de citas. Conflictos y persistencia se resuelven mediante el puerto de repositorio. */
public final class AppointmentScheduler {
    private final AppointmentRepository repository;
    public AppointmentScheduler(AppointmentRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Appointment schedule(ScheduleAppointmentCommand command) {
        Instant now = Instant.now();
        if (command.startsAt().isBefore(now)) throw new IllegalArgumentException("No se puede agendar en el pasado");
        if (repository.hasConflict(command.doctorId(), command.officeId(), command.startsAt(), command.endsAt())) throw new IllegalStateException("El médico o consultorio ya está ocupado");
        Appointment appointment = Appointment.schedule(command.patientId(), command.doctorId(), command.branchId(), command.officeId(), command.serviceId(), command.startsAt(), command.endsAt(), now);
        return repository.save(appointment);
    }
    public Appointment changeStatus(AppointmentId id, AppointmentStatus status) {
        Appointment current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        return repository.save(current.withStatus(status));
    }
}
