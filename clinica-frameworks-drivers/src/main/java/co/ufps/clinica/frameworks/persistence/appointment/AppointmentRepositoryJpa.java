package co.ufps.clinica.frameworks.persistence.appointment;

import co.ufps.clinica.domain.appointment.*;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.domain.catalog.ServiceId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Adaptador JPA del repositorio de citas. */
@ApplicationScoped
public class AppointmentRepositoryJpa implements AppointmentRepository {
    @PersistenceContext EntityManager entityManager;

    @Override public boolean hasConflict(DoctorId doctorId, OfficeId officeId, Instant from, Instant until) {
        Long count = entityManager.createQuery("SELECT COUNT(a) FROM AppointmentJpa a WHERE a.status NOT IN :excluded AND a.startsAt < :until AND a.endsAt > :from AND (a.doctorId = :doctorId OR a.officeId = :officeId)", Long.class)
                .setParameter("excluded", List.of(AppointmentStatus.CANCELLED.name(), AppointmentStatus.NO_SHOW.name()))
                .setParameter("until", until).setParameter("from", from)
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
}
