package co.ufps.clinica.application.appointment;

import co.ufps.clinica.application.port.out.appointment.WaitlistRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.communication.NotificationGateway;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.WaitlistEntry;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import co.ufps.clinica.domain.appointment.WaitlistStatus;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.StaffStatus;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class WaitlistService {
    private final WaitlistRepository repository;
    private final PatientRepository patients;
    private final NotificationGateway notifications;
    private final DoctorRepository doctors;
    private final CatalogRepository catalog;
    public WaitlistService(WaitlistRepository repository, PatientRepository patients, DoctorRepository doctors,
                           CatalogRepository catalog, NotificationGateway notifications) {
        this.repository = Objects.requireNonNull(repository);
        this.patients = Objects.requireNonNull(patients);
        this.doctors = Objects.requireNonNull(doctors);
        this.catalog = Objects.requireNonNull(catalog);
        this.notifications = Objects.requireNonNull(notifications);
    }
    public WaitlistEntry join(UserId userId, JoinWaitlistCommand command) {
        Instant now = Instant.now();
        if (command == null || command.requestedFrom() == null || command.requestedUntil() == null
                || !command.requestedFrom().isBefore(command.requestedUntil())
                || !command.requestedFrom().isAfter(now) || !command.requestedUntil().isAfter(now))
            throw new IllegalArgumentException("El intervalo de disponibilidad debe ser futuro y válido");
        if (command.doctorId() != null)
            doctors.findById(command.doctorId()).filter(d -> d.status() == StaffStatus.ACTIVE)
                    .orElseThrow(() -> new NoSuchElementException("Médico activo no encontrado"));
        if (command.specialtyId() != null)
            catalog.findSpecialtyById(command.specialtyId()).filter(s -> s.active())
                    .orElseThrow(() -> new NoSuchElementException("Especialidad activa no encontrada"));
        PatientId patientId = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente")).id();
        repository.expireBefore(now);
        WaitlistEntry entry = new WaitlistEntry(WaitlistEntryId.generate(), patientId, command.doctorId(), command.specialtyId(),
                command.requestedFrom(), command.requestedUntil(), now, WaitlistStatus.WAITING);
        return repository.save(entry);
    }
    public List<WaitlistEntry> mine(UserId userId) {
        repository.expireBefore(Instant.now());
        PatientId patientId = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente")).id();
        return repository.findByPatient(patientId);
    }
    public WaitlistEntry cancel(UserId userId, WaitlistEntryId id) {
        repository.expireBefore(Instant.now());
        PatientId patientId = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente")).id();
        WaitlistEntry entry = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Solicitud de espera no encontrada"));
        if (!entry.patientId().equals(patientId)) throw new SecurityException("No puede cancelar la solicitud de otro paciente");
        if (entry.status() != WaitlistStatus.WAITING && entry.status() != WaitlistStatus.OFFERED)
            throw new IllegalStateException("La solicitud ya no se puede cancelar");
        return repository.save(entry.withStatus(WaitlistStatus.CANCELLED));
    }
    public WaitlistEntry offerNext(Appointment cancelledAppointment) {
        repository.expireBefore(Instant.now());
        WaitlistEntry offer = repository.offerNextFor(cancelledAppointment.doctorId(), cancelledAppointment.startsAt()).orElse(null);
        if (offer != null) notifications.sendWaitlistOffer(offer, cancelledAppointment);
        return offer;
    }
    public WaitlistEntry requireOwnedOffer(UserId userId, WaitlistEntryId id) {
        repository.expireBefore(Instant.now());
        PatientId patientId = patients.findByUserId(userId).orElseThrow(() -> new SecurityException("La cuenta no está vinculada a un paciente")).id();
        WaitlistEntry entry = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Oferta no encontrada"));
        if (!entry.patientId().equals(patientId) || entry.status() != WaitlistStatus.OFFERED)
            throw new SecurityException("La oferta no pertenece al paciente o ya no está activa");
        return entry;
    }
    public WaitlistEntry markBooked(WaitlistEntry entry) { return repository.save(entry.withStatus(WaitlistStatus.BOOKED)); }
}
