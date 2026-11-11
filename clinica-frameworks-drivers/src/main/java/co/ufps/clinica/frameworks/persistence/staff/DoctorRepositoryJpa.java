package co.ufps.clinica.frameworks.persistence.staff;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.*;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DoctorRepositoryJpa implements DoctorRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Doctor save(Doctor doctor) {
        entityManager.merge(toJpa(doctor));
        return doctor;
    }

    @Override public Optional<Doctor> findById(DoctorId id) {
        DoctorJpa jpa = entityManager.find(DoctorJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }

    @Override public List<Doctor> findAllActive() {
        return entityManager.createQuery("SELECT d FROM DoctorJpa d WHERE d.status = :status", DoctorJpa.class)
                .setParameter("status", StaffStatus.ACTIVE.name()).getResultList().stream().map(this::toDomain).toList();
    }

    @Override public List<Doctor> findBySpecialty(SpecialtyId specialtyId) {
        return entityManager.createQuery("SELECT d FROM DoctorJpa d WHERE d.specialtyId = :sid", DoctorJpa.class)
                .setParameter("sid", specialtyId.value()).getResultList().stream().map(this::toDomain).toList();
    }

    @Override @Transactional public DoctorAvailability saveAvailability(DoctorAvailability availability) {
        entityManager.merge(toJpa(availability));
        return availability;
    }

    @Override public List<DoctorAvailability> findByDoctor(DoctorId doctorId) {
        return entityManager.createQuery("SELECT a FROM DoctorAvailabilityJpa a WHERE a.doctorId = :did", DoctorAvailabilityJpa.class)
                .setParameter("did", doctorId.value()).getResultList().stream().map(this::toDomainAvail).toList();
    }

    private DoctorJpa toJpa(Doctor d) {
        DoctorJpa jpa = new DoctorJpa();
        jpa.setId(d.id().value()); jpa.setUserId(d.userId().value());
        jpa.setLicenseNumber(d.licenseNumber()); jpa.setFirstName(d.firstName());
        jpa.setLastName(d.lastName()); jpa.setSpecialtyId(d.specialtyId().value());
        jpa.setStatus(d.status().name());
        return jpa;
    }
    private Doctor toDomain(DoctorJpa jpa) {
        return new Doctor(DoctorId.of(jpa.getId()), UserId.of(jpa.getUserId()),
                jpa.getLicenseNumber(), jpa.getFirstName(), jpa.getLastName(),
                SpecialtyId.of(jpa.getSpecialtyId()), StaffStatus.valueOf(jpa.getStatus()));
    }

    private DoctorAvailabilityJpa toJpa(DoctorAvailability a) {
        DoctorAvailabilityJpa jpa = new DoctorAvailabilityJpa();
        jpa.setId(a.id().value()); jpa.setDoctorId(a.doctorId().value());
        jpa.setBranchId(a.branchId().value()); jpa.setDay(a.day().name());
        jpa.setStartsAt(a.startsAt()); jpa.setEndsAt(a.endsAt());
        jpa.setValidFrom(a.validFrom()); jpa.setValidUntil(a.validUntil());
        return jpa;
    }
    private DoctorAvailability toDomainAvail(DoctorAvailabilityJpa jpa) {
        return new DoctorAvailability(DoctorAvailabilityId.of(jpa.getId()), DoctorId.of(jpa.getDoctorId()),
                co.ufps.clinica.domain.clinic.BranchId.of(jpa.getBranchId()),
                java.time.DayOfWeek.valueOf(jpa.getDay()), jpa.getStartsAt(), jpa.getEndsAt(),
                jpa.getValidFrom(), jpa.getValidUntil());
    }
}
