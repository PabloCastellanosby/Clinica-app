package co.ufps.clinica.frameworks.persistence.catalog;

import co.ufps.clinica.domain.catalog.*;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CatalogRepositoryJpa implements CatalogRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Specialty saveSpecialty(Specialty specialty) {
        entityManager.merge(toJpa(specialty));
        return specialty;
    }

    @Override public Optional<Specialty> findSpecialtyById(SpecialtyId id) {
        SpecialtyJpa jpa = entityManager.find(SpecialtyJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainSpecialty);
    }

    @Override public List<Specialty> findAllActiveSpecialties() {
        return entityManager.createQuery("SELECT s FROM SpecialtyJpa s WHERE s.active = true", SpecialtyJpa.class)
                .getResultList().stream().map(this::toDomainSpecialty).toList();
    }

    @Override @Transactional public Service saveService(Service service) {
        entityManager.merge(toJpa(service));
        return service;
    }

    @Override public Optional<Service> findServiceById(ServiceId id) {
        ServiceJpa jpa = entityManager.find(ServiceJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomainService);
    }

    @Override public List<Service> findAllActiveServices() {
        return entityManager.createQuery("SELECT s FROM ServiceJpa s WHERE s.active = true", ServiceJpa.class)
                .getResultList().stream().map(this::toDomainService).toList();
    }

    @Override public List<Service> findServicesBySpecialty(SpecialtyId specialtyId) {
        return entityManager.createQuery("SELECT s FROM ServiceJpa s WHERE s.specialtyId = :sid", ServiceJpa.class)
                .setParameter("sid", specialtyId.value()).getResultList().stream().map(this::toDomainService).toList();
    }

    private SpecialtyJpa toJpa(Specialty s) {
        SpecialtyJpa jpa = new SpecialtyJpa();
        jpa.setId(s.id().value()); jpa.setName(s.name());
        jpa.setDescription(s.description()); jpa.setActive(s.active());
        return jpa;
    }
    private Specialty toDomainSpecialty(SpecialtyJpa jpa) {
        return new Specialty(SpecialtyId.of(jpa.getId()), jpa.getName(), jpa.getDescription(), jpa.isActive());
    }

    private ServiceJpa toJpa(Service s) {
        ServiceJpa jpa = new ServiceJpa();
        jpa.setId(s.id().value()); jpa.setSpecialtyId(s.specialtyId().value());
        jpa.setName(s.name()); jpa.setDescription(s.description());
        jpa.setPriceMinorUnits(s.priceMinorUnits()); jpa.setCurrency(s.currency());
        jpa.setActive(s.active());
        return jpa;
    }
    private Service toDomainService(ServiceJpa jpa) {
        return new Service(ServiceId.of(jpa.getId()), SpecialtyId.of(jpa.getSpecialtyId()),
                jpa.getName(), jpa.getDescription(), jpa.getPriceMinorUnits(),
                jpa.getCurrency(), jpa.isActive());
    }
}
