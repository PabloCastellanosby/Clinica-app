package co.ufps.clinica.frameworks.persistence.clinic;

import co.ufps.clinica.domain.clinic.*;
import co.ufps.clinica.application.port.out.clinic.ClinicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClinicRepositoryJpa implements ClinicRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Clinic saveClinic(Clinic clinic) {
        entityManager.merge(toJpa(clinic));
        return clinic;
    }

    @Override public Optional<Clinic> findById(ClinicId id) {
        ClinicJpa jpa = entityManager.find(ClinicJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainClinic);
    }

    @Override public List<Clinic> findAllActive() {
        return entityManager.createQuery("SELECT c FROM ClinicJpa c WHERE c.active = true", ClinicJpa.class)
                .getResultList().stream().map(this::toDomainClinic).toList();
    }

    @Override @Transactional public Branch saveBranch(Branch branch) {
        entityManager.merge(toJpa(branch));
        return branch;
    }

    @Override public Optional<Branch> findBranchById(BranchId id) {
        BranchJpa jpa = entityManager.find(BranchJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainBranch);
    }

    @Override public List<Branch> findBranchesByClinic(ClinicId clinicId) {
        return entityManager.createQuery("SELECT b FROM BranchJpa b WHERE b.clinicId = :cid", BranchJpa.class)
                .setParameter("cid", clinicId.value()).getResultList().stream().map(this::toDomainBranch).toList();
    }

    @Override @Transactional public Office saveOffice(Office office) {
        entityManager.merge(toJpa(office));
        return office;
    }

    @Override public Optional<Office> findOfficeById(OfficeId id) {
        OfficeJpa jpa = entityManager.find(OfficeJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainOffice);
    }

    @Override public List<Office> findOfficesByBranch(BranchId branchId) {
        return entityManager.createQuery("SELECT o FROM OfficeJpa o WHERE o.branchId = :bid", OfficeJpa.class)
                .setParameter("bid", branchId.value()).getResultList().stream().map(this::toDomainOffice).toList();
    }

    private ClinicJpa toJpa(Clinic c) {
        ClinicJpa jpa = new ClinicJpa();
        jpa.setId(c.id().value()); jpa.setName(c.name());
        jpa.setTaxId(c.taxId()); jpa.setEmail(c.email());
        jpa.setPhone(c.phone()); jpa.setActive(c.active());
        return jpa;
    }
    private Clinic toDomainClinic(ClinicJpa jpa) {
        return new Clinic(ClinicId.of(jpa.getId()), jpa.getName(), jpa.getTaxId(),
                jpa.getEmail(), jpa.getPhone(), jpa.isActive());
    }

    private BranchJpa toJpa(Branch b) {
        BranchJpa jpa = new BranchJpa();
        jpa.setId(b.id().value()); jpa.setClinicId(b.clinicId().value());
        jpa.setName(b.name()); jpa.setAddress(b.address());
        jpa.setPhone(b.phone()); jpa.setTimeZone(b.timeZone().toString());
        jpa.setActive(b.active());
        return jpa;
    }
    private Branch toDomainBranch(BranchJpa jpa) {
        return new Branch(BranchId.of(jpa.getId()), ClinicId.of(jpa.getClinicId()),
                jpa.getName(), jpa.getAddress(), jpa.getPhone(),
                java.time.ZoneId.of(jpa.getTimeZone()), jpa.isActive());
    }

    private OfficeJpa toJpa(Office o) {
        OfficeJpa jpa = new OfficeJpa();
        jpa.setId(o.id().value()); jpa.setBranchId(o.branchId().value());
        jpa.setName(o.name()); jpa.setType(o.type());
        jpa.setCapacity(o.capacity()); jpa.setActive(o.active());
        return jpa;
    }
    private Office toDomainOffice(OfficeJpa jpa) {
        return new Office(OfficeId.of(jpa.getId()), BranchId.of(jpa.getBranchId()),
                jpa.getName(), jpa.getType(), jpa.getCapacity(), jpa.isActive());
    }
}
