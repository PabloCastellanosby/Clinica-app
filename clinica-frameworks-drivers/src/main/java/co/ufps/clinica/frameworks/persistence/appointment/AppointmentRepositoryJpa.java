package co.ufps.clinica.frameworks.persistence.appointment;

import co.ufps.clinica.domain.appointment.*;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.appointment.WaitlistRepository;
import co.ufps.clinica.domain.catalog.ServiceId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.frameworks.persistence.staff.DoctorJpa;
import co.ufps.clinica.frameworks.persistence.clinic.OfficeJpa;
import co.ufps.clinica.frameworks.persistence.patient.PatientJpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Adaptador JPA del repositorio de citas. */
@ApplicationScoped
public class AppointmentRepositoryJpa implements AppointmentRepository, WaitlistRepository {
    @PersistenceContext EntityManager entityManager;

    @Override public boolean hasConflict(PatientId patientId, DoctorId doctorId, OfficeId officeId, Instant from, Instant until) {
        Long count = entityManager.createQuery("SELECT COUNT(a) FROM AppointmentJpa a WHERE a.status NOT IN :excluded AND a.startsAt < :until AND a.endsAt > :from AND (a.patientId = :patientId OR a.doctorId = :doctorId OR a.officeId = :officeId)", Long.class)
                .setParameter("excluded", List.of(AppointmentStatus.CANCELLED.name(), AppointmentStatus.NO_SHOW.name()))
                .setParameter("until", until).setParameter("from", from)
                .setParameter("patientId", patientId.value())
                .setParameter("doctorId", doctorId.value()).setParameter("officeId", officeId.value())
                .getSingleResult();
        return count > 0;
    }

    @Override public Optional<Appointment> findById(AppointmentId id) {
        AppointmentJpa jpa = entityManager.find(AppointmentJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }

    @Override @Transactional public Appointment save(Appointment appointment) {
        entityManager.merge(toJpa(appointment));
        return appointment;
    }

    @Override @Transactional public Appointment createIfAvailable(Appointment appointment, AppointmentReminder reminder) {
        lockAndCheckAvailability(appointment);
        entityManager.persist(toJpa(appointment));
        entityManager.persist(toJpa(reminder));
        return appointment;
    }

    @Override @Transactional public Appointment createFromWaitlistIfAvailable(Appointment appointment,
            AppointmentReminder reminder, WaitlistEntryId offerId, PatientId patientId) {
        lockAndCheckAvailability(appointment);
        WaitlistEntryJpa offer = entityManager.find(WaitlistEntryJpa.class, offerId.value(),
                jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        if (offer == null || !offer.getPatientId().equals(patientId.value())
                || !WaitlistStatus.OFFERED.name().equals(offer.getStatus())
                || appointment.startsAt().isBefore(offer.getRequestedFrom())
                || appointment.endsAt().isAfter(offer.getRequestedUntil()))
            throw new IllegalStateException("La oferta ya no está vigente o el horario no coincide");

        entityManager.persist(toJpa(appointment));
        entityManager.persist(toJpa(reminder));
        offer.setStatus(WaitlistStatus.BOOKED.name());
        return appointment;
    }

    private void lockAndCheckAvailability(Appointment appointment) {
        if (entityManager.find(PatientJpa.class, appointment.patientId().value(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) == null)
            throw new java.util.NoSuchElementException("Paciente no encontrado");
        if (entityManager.find(DoctorJpa.class, appointment.doctorId().value(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) == null)
            throw new java.util.NoSuchElementException("Médico no encontrado");
        if (entityManager.find(OfficeJpa.class, appointment.officeId().value(), jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) == null)
            throw new java.util.NoSuchElementException("Consultorio no encontrado");
        if (hasConflict(appointment.patientId(), appointment.doctorId(), appointment.officeId(), appointment.startsAt(), appointment.endsAt()))
            throw new IllegalStateException("El paciente, médico o consultorio ya tiene otra cita en ese horario");
    }

    @Override @Transactional public AppointmentReminder saveReminder(AppointmentReminder reminder) {
        entityManager.merge(toJpa(reminder));
        return reminder;
    }

    @Override @Transactional public List<AppointmentReminder> claimDueReminders(Instant now, int limit) {
        Instant staleBefore = now.minusSeconds(600);
        List<AppointmentReminderJpa> due = entityManager.createQuery(
                "SELECT r FROM AppointmentReminderJpa r WHERE r.sentAt IS NULL AND r.attempts < 5 AND r.scheduledAt <= :now AND (r.lastAttemptAt IS NULL OR r.lastAttemptAt <= :staleBefore) AND EXISTS (SELECT a.id FROM AppointmentJpa a WHERE a.id = r.appointmentId AND a.status = :confirmed AND a.startsAt > :now) ORDER BY r.scheduledAt",
                AppointmentReminderJpa.class)
                .setParameter("now", now)
                .setParameter("staleBefore", staleBefore)
                .setParameter("confirmed", AppointmentStatus.CONFIRMED.name())
                .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(Math.max(1, Math.min(limit, 500))).getResultList();
        for (AppointmentReminderJpa jpa : due) {
            AppointmentReminder claimed = toDomainReminder(jpa).claimedAt(now);
            jpa.setAttempts(claimed.attempts());
            jpa.setLastAttemptAt(claimed.lastAttemptAt());
        }
        entityManager.flush();
        return due.stream().map(this::toDomainReminder).toList();
    }

    @Override public List<Appointment> findForDoctor(DoctorId doctorId, Instant from, Instant until) {
        return entityManager.createQuery("SELECT a FROM AppointmentJpa a WHERE a.doctorId = :doctorId AND a.startsAt < :until AND a.endsAt > :from ORDER BY a.startsAt", AppointmentJpa.class)
                .setParameter("doctorId", doctorId.value()).setParameter("until", until).setParameter("from", from)
                .getResultList().stream().map(this::toDomain).toList();
    }

    @Override public List<Appointment> findForPatient(PatientId patientId, Instant from, Instant until) {
        return entityManager.createQuery("SELECT a FROM AppointmentJpa a WHERE a.patientId = :patientId AND a.startsAt < :until AND a.endsAt > :from ORDER BY a.startsAt", AppointmentJpa.class)
                .setParameter("patientId", patientId.value()).setParameter("until", until).setParameter("from", from)
                .getResultList().stream().map(this::toDomain).toList();
    }

    @Override public boolean hasClinicalRelationship(DoctorId doctorId, PatientId patientId) {
        Long count = entityManager.createQuery("SELECT COUNT(a) FROM AppointmentJpa a WHERE a.doctorId = :doctorId AND a.patientId = :patientId AND a.status IN :eligible", Long.class)
                .setParameter("doctorId", doctorId.value()).setParameter("patientId", patientId.value())
                .setParameter("eligible", List.of(AppointmentStatus.CHECKED_IN.name(), AppointmentStatus.IN_CONSULTATION.name(), AppointmentStatus.COMPLETED.name()))
                .getSingleResult();
        return count > 0;
    }

    private AppointmentJpa toJpa(Appointment appointment) {
        AppointmentJpa jpa = new AppointmentJpa();
        jpa.setId(appointment.id().value()); jpa.setPatientId(appointment.patientId().value());
        jpa.setDoctorId(appointment.doctorId().value()); jpa.setBranchId(appointment.branchId().value());
        jpa.setOfficeId(appointment.officeId().value());
        jpa.setServiceId(appointment.serviceId() == null ? null : appointment.serviceId().value());
        jpa.setStartsAt(appointment.startsAt()); jpa.setEndsAt(appointment.endsAt());
        jpa.setStatus(appointment.status().name()); jpa.setCancellationReason(appointment.cancellationReason());
        jpa.setCreatedAt(appointment.createdAt());
        return jpa;
    }
    private Appointment toDomain(AppointmentJpa jpa) {
        return new Appointment(AppointmentId.of(jpa.getId()), PatientId.of(jpa.getPatientId()), DoctorId.of(jpa.getDoctorId()),
                BranchId.of(jpa.getBranchId()), OfficeId.of(jpa.getOfficeId()),
                jpa.getServiceId() == null ? null : ServiceId.of(jpa.getServiceId()), jpa.getStartsAt(), jpa.getEndsAt(),
                AppointmentStatus.valueOf(jpa.getStatus()), jpa.getCancellationReason(), jpa.getCreatedAt());
    }

    @Override @Transactional public WaitlistEntry save(WaitlistEntry entry) {
        WaitlistEntryJpa jpa = new WaitlistEntryJpa();
        jpa.setId(entry.id().value());
        jpa.setPatientId(entry.patientId().value());
        jpa.setDoctorId(entry.doctorId() == null ? null : entry.doctorId().value());
        jpa.setSpecialtyId(entry.specialtyId() == null ? null : entry.specialtyId().value());
        jpa.setRequestedFrom(entry.requestedFrom());
        jpa.setRequestedUntil(entry.requestedUntil());
        jpa.setCreatedAt(entry.createdAt());
        jpa.setStatus(entry.status().name());
        entityManager.merge(jpa);
        return entry;
    }

    @Override public Optional<WaitlistEntry> findById(WaitlistEntryId id) {
        return Optional.ofNullable(entityManager.find(WaitlistEntryJpa.class, id.value())).map(this::toDomainWaitlist);
    }

    @Override public List<WaitlistEntry> findByPatient(PatientId patientId) {
        return entityManager.createQuery("SELECT w FROM WaitlistEntryJpa w WHERE w.patientId = :patientId ORDER BY w.createdAt DESC", WaitlistEntryJpa.class)
                .setParameter("patientId", patientId.value()).getResultList().stream().map(this::toDomainWaitlist).toList();
    }

    @Override @Transactional public void expireBefore(Instant now) {
        entityManager.createQuery("UPDATE WaitlistEntryJpa w SET w.status = :expired WHERE w.status IN :openStates AND w.requestedUntil <= :now")
                .setParameter("expired", WaitlistStatus.EXPIRED.name())
                .setParameter("openStates", List.of(WaitlistStatus.WAITING.name(), WaitlistStatus.OFFERED.name()))
                .setParameter("now", now).executeUpdate();
    }

    @Override @Transactional public Optional<WaitlistEntry> offerNextFor(DoctorId doctorId, Instant slotStart) {
        List<WaitlistEntryJpa> candidates = entityManager.createQuery("SELECT w FROM WaitlistEntryJpa w WHERE w.status = :waiting AND w.requestedFrom <= :slotStart AND w.requestedUntil > :slotStart AND (w.doctorId = :doctorId OR (w.doctorId IS NULL AND w.specialtyId = (SELECT d.specialtyId FROM DoctorJpa d WHERE d.id = :doctorId))) ORDER BY w.createdAt", WaitlistEntryJpa.class)
                .setParameter("waiting", WaitlistStatus.WAITING.name()).setParameter("slotStart", slotStart)
                .setParameter("doctorId", doctorId.value()).setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1).getResultList();
        if (candidates.isEmpty()) return Optional.empty();
        WaitlistEntry offered = toDomainWaitlist(candidates.get(0)).withStatus(WaitlistStatus.OFFERED);
        save(offered);
        return Optional.of(offered);
    }

    private WaitlistEntry toDomainWaitlist(WaitlistEntryJpa jpa) {
        return new WaitlistEntry(WaitlistEntryId.of(jpa.getId()), PatientId.of(jpa.getPatientId()),
                jpa.getDoctorId() == null ? null : DoctorId.of(jpa.getDoctorId()),
                jpa.getSpecialtyId() == null ? null : co.ufps.clinica.domain.catalog.SpecialtyId.of(jpa.getSpecialtyId()),
                jpa.getRequestedFrom(), jpa.getRequestedUntil(), jpa.getCreatedAt(), WaitlistStatus.valueOf(jpa.getStatus()));
    }

    private AppointmentReminderJpa toJpa(AppointmentReminder reminder) {
        AppointmentReminderJpa jpa = new AppointmentReminderJpa();
        jpa.setId(reminder.id().value());
        jpa.setAppointmentId(reminder.appointmentId().value());
        jpa.setScheduledAt(reminder.scheduledAt());
        jpa.setSentAt(reminder.sentAt());
        jpa.setChannel(reminder.channel().name());
        jpa.setAttempts(reminder.attempts());
        jpa.setLastAttemptAt(reminder.lastAttemptAt());
        return jpa;
    }

    private AppointmentReminder toDomainReminder(AppointmentReminderJpa jpa) {
        return new AppointmentReminder(AppointmentReminderId.of(jpa.getId()), AppointmentId.of(jpa.getAppointmentId()),
                jpa.getScheduledAt(), jpa.getSentAt(), co.ufps.clinica.domain.communication.NotificationChannel.valueOf(jpa.getChannel()),
                jpa.getAttempts(), jpa.getLastAttemptAt());
    }
}
