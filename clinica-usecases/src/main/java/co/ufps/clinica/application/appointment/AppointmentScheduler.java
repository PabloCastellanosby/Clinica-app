package co.ufps.clinica.application.appointment;

import co.ufps.clinica.domain.appointment.*;
import co.ufps.clinica.domain.communication.NotificationChannel;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.application.port.out.clinic.ClinicRepository;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import co.ufps.clinica.application.communication.NotificationGateway;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import co.ufps.clinica.domain.staff.StaffStatus;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.*;

/** Agenda de citas. Conflictos y persistencia se resuelven mediante el puerto de repositorio. */
public final class AppointmentScheduler {
    private final AppointmentRepository repository;
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final ClinicRepository clinics;
    private final CatalogRepository catalog;
    private final NotificationGateway notifications;
    private final WaitlistService waitlist;
    public AppointmentScheduler(AppointmentRepository repository, PatientRepository patients, DoctorRepository doctors,
                                ClinicRepository clinics, CatalogRepository catalog, NotificationGateway notifications, WaitlistService waitlist) {
        this.repository = Objects.requireNonNull(repository);
        this.patients = Objects.requireNonNull(patients);
        this.doctors = Objects.requireNonNull(doctors);
        this.clinics = Objects.requireNonNull(clinics);
        this.catalog = Objects.requireNonNull(catalog);
        this.notifications = Objects.requireNonNull(notifications);
        this.waitlist = Objects.requireNonNull(waitlist);
    }
    public Appointment schedule(ScheduleAppointmentCommand command) {
        return schedule(command, null, null);
    }

    private Appointment schedule(ScheduleAppointmentCommand command, WaitlistEntryId offerId, PatientId offeredPatientId) {
        Objects.requireNonNull(command, "La solicitud de cita es obligatoria");
        Instant now = Instant.now();
        if (command.patientId() == null || command.doctorId() == null || command.branchId() == null || command.officeId() == null
                || command.startsAt() == null || command.endsAt() == null || !command.startsAt().isBefore(command.endsAt())
                || !command.startsAt().isAfter(now))
            throw new IllegalArgumentException("La cita debe tener paciente, médico, sede, consultorio y un intervalo futuro válido");
        patients.findById(command.patientId()).orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
        var doctor = doctors.findById(command.doctorId()).filter(d -> d.status() == StaffStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException("Médico activo no encontrado"));
        var branch = clinics.findBranchById(command.branchId()).filter(co -> co.active())
                .orElseThrow(() -> new NoSuchElementException("Sede activa no encontrada"));
        var office = clinics.findOfficeById(command.officeId()).filter(co -> co.active() && co.branchId().equals(command.branchId()))
                .orElseThrow(() -> new NoSuchElementException("Consultorio activo no encontrado en la sede indicada"));
        if (command.serviceId() != null) {
            var service = catalog.findServiceById(command.serviceId()).filter(s -> s.active())
                    .orElseThrow(() -> new NoSuchElementException("Servicio activo no encontrado"));
            if (service.specialtyId() != null && !service.specialtyId().equals(doctor.specialtyId()))
                throw new IllegalArgumentException("El servicio no corresponde a la especialidad del médico");
        }
        var start = command.startsAt().atZone(branch.timeZone());
        var end = command.endsAt().atZone(branch.timeZone());
        if (!start.toLocalDate().equals(end.toLocalDate())) throw new IllegalArgumentException("La cita debe finalizar el mismo día local en que comienza");
        LocalDate appointmentDate = start.toLocalDate();
        LocalTime localStart = start.toLocalTime();
        LocalTime localEnd = end.toLocalTime();
        if (clinics.findHolidaysByBranch(command.branchId()).stream().anyMatch(h -> h.date().equals(appointmentDate)))
            throw new IllegalStateException("La sede está cerrada en la fecha seleccionada");
        boolean clinicOpen = clinics.findOpeningHoursByBranch(command.branchId()).stream()
                .anyMatch(h -> h.day() == start.getDayOfWeek() && !localStart.isBefore(h.opensAt()) && !localEnd.isAfter(h.closesAt()));
        if (!clinicOpen) throw new IllegalStateException("La sede no tiene horario de atención para ese intervalo");
        boolean doctorAvailable = doctors.findByDoctor(command.doctorId()).stream()
                .anyMatch(a -> a.branchId().equals(command.branchId()) && a.day() == start.getDayOfWeek()
                        && !appointmentDate.isBefore(a.validFrom()) && (a.validUntil() == null || !appointmentDate.isAfter(a.validUntil()))
                        && !localStart.isBefore(a.startsAt()) && !localEnd.isAfter(a.endsAt()));
        if (!doctorAvailable) throw new IllegalStateException("El médico no está disponible en ese horario");
        Appointment appointment = Appointment.schedule(command.patientId(), command.doctorId(), command.branchId(), command.officeId(), command.serviceId(), command.startsAt(), command.endsAt(), now);
        AppointmentReminder reminder = new AppointmentReminder(AppointmentReminderId.generate(), appointment.id(),
                command.startsAt().minus(java.time.Duration.ofHours(24)), null, NotificationChannel.EMAIL, 0, null);
        Appointment saved = offerId == null
                ? repository.createIfAvailable(appointment, reminder)
                : repository.createFromWaitlistIfAvailable(appointment, reminder, offerId, offeredPatientId);
        notifications.sendAppointmentConfirmation(saved);
        return saved;
    }
    public Appointment changeStatus(AppointmentId id, AppointmentStatus status) {
        return changeStatus(id, status, null);
    }
    public Appointment changeStatus(AppointmentId id, AppointmentStatus status, String reason) {
        Appointment current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        Appointment updated = repository.save(status == AppointmentStatus.CANCELLED
                ? current.cancel(reason == null || reason.isBlank() ? "Cancelada por recepción" : reason)
                : current.withStatus(status));
        if (status == AppointmentStatus.CANCELLED) waitlist.offerNext(current);
        return updated;
    }
    public Appointment changeStatusByDoctor(co.ufps.clinica.domain.identity.UserId userId, AppointmentId id,
            AppointmentStatus status) {
        var doctor = doctors.findByUserId(userId).filter(d -> d.status() == StaffStatus.ACTIVE)
                .orElseThrow(() -> new SecurityException("Cuenta médica inactiva o sin perfil profesional"));
        Appointment current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        if (!current.doctorId().equals(doctor.id())) throw new SecurityException("El médico solo puede gestionar sus propias citas");
        if (status != AppointmentStatus.IN_CONSULTATION && status != AppointmentStatus.COMPLETED)
            throw new SecurityException("El médico solo puede iniciar o cerrar una consulta");
        return changeStatus(id, status, null);
    }
    public Appointment cancelForPatient(co.ufps.clinica.domain.identity.UserId userId, AppointmentId id, String reason) {
        var patient = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente"));
        Appointment current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
        if (!current.patientId().equals(patient.id())) throw new SecurityException("No puede cancelar una cita de otro paciente");
        if (!current.startsAt().isAfter(Instant.now())) throw new IllegalStateException("No se puede cancelar una cita que ya comenzó");
        return changeStatus(id, AppointmentStatus.CANCELLED, reason);
    }
    public Appointment bookWaitlistOffer(co.ufps.clinica.domain.identity.UserId userId, WaitlistEntryId entryId,
                                         ScheduleAppointmentCommand command) {
        if (userId == null || entryId == null || command == null || command.doctorId() == null
                || command.startsAt() == null || command.endsAt() == null)
            throw new IllegalArgumentException("La solicitud para reservar la oferta está incompleta");
        var offer = waitlist.requireOwnedOffer(userId, entryId);
        var doctor = doctors.findById(command.doctorId()).orElseThrow(() -> new NoSuchElementException("Médico no encontrado"));
        if (offer.doctorId() != null && !offer.doctorId().equals(command.doctorId()))
            throw new IllegalArgumentException("La oferta corresponde a otro médico");
        if (offer.specialtyId() != null && !offer.specialtyId().equals(doctor.specialtyId()))
            throw new IllegalArgumentException("La oferta corresponde a otra especialidad");
        if (command.startsAt().isBefore(offer.requestedFrom()) || command.endsAt().isAfter(offer.requestedUntil()))
            throw new IllegalArgumentException("El horario está fuera del intervalo solicitado en la lista de espera");
        ScheduleAppointmentCommand owned = new ScheduleAppointmentCommand(offer.patientId(), command.doctorId(),
                command.branchId(), command.officeId(), command.serviceId(), command.startsAt(), command.endsAt());
        return schedule(owned, offer.id(), offer.patientId());
    }
    public Appointment scheduleForUser(co.ufps.clinica.domain.identity.UserId userId, ScheduleAppointmentCommand command) {
        var patient = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente"));
        ScheduleAppointmentCommand ownedCommand = new ScheduleAppointmentCommand(patient.id(), command.doctorId(),
                command.branchId(), command.officeId(), command.serviceId(), command.startsAt(), command.endsAt());
        return schedule(ownedCommand);
    }
    public List<Appointment> forPatientUser(co.ufps.clinica.domain.identity.UserId userId, Instant from, Instant until) {
        var patient = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente"));
        return forPatient(patient.id(), from, until);
    }
    public List<Appointment> forDoctor(DoctorId doctorId, Instant from, Instant until) {
        validateRange(from, until);
        return repository.findForDoctor(doctorId, from, until);
    }
    public List<Appointment> forDoctorUser(co.ufps.clinica.domain.identity.UserId userId, Instant from, Instant until) {
        var doctor = doctors.findByUserId(userId).filter(d -> d.status() == StaffStatus.ACTIVE)
                .orElseThrow(() -> new SecurityException("Cuenta médica inactiva o sin perfil profesional"));
        return forDoctor(doctor.id(), from, until);
    }
    public List<Appointment> forPatient(co.ufps.clinica.domain.patient.PatientId patientId, Instant from, Instant until) {
        validateRange(from, until);
        return repository.findForPatient(patientId, from, until);
    }
    private void validateRange(Instant from, Instant until) {
        if (from == null || until == null || !from.isBefore(until)) throw new IllegalArgumentException("El rango temporal debe ser válido");
    }
}
