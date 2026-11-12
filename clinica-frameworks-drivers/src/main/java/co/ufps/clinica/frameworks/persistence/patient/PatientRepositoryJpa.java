package co.ufps.clinica.frameworks.persistence.patient;

import co.ufps.clinica.domain.patient.*;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.identity.UserId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Adaptador JPA del repositorio de pacientes. */
@ApplicationScoped
public class PatientRepositoryJpa implements PatientRepository {
    @PersistenceContext EntityManager entityManager;

    @Override public Optional<Patient> findByDocument(String type, String number) {
        return entityManager.createQuery("SELECT p FROM PatientJpa p WHERE p.documentType = :type AND p.documentNumber = :number", PatientJpa.class)
                .setParameter("type", type).setParameter("number", number)
                .getResultList().stream().findFirst().map(this::toDomain);
    }

    @Override public Optional<Patient> findById(PatientId id) {
        PatientJpa jpa = entityManager.find(PatientJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }

    @Override public Optional<Patient> findByUserId(UserId userId) {
        return entityManager.createQuery("SELECT p FROM PatientJpa p WHERE p.userId = :userId", PatientJpa.class)
                .setParameter("userId", userId.value()).getResultStream().findFirst().map(this::toDomain);
    }

    @Override @Transactional public Patient save(Patient patient) {
        entityManager.merge(toJpa(patient));
        return patient;
    }

    @Override public List<Patient> search(String query) {
        String term = "%" + (query == null ? "" : query.trim().toLowerCase(Locale.ROOT)) + "%";
        return entityManager.createQuery("SELECT p FROM PatientJpa p WHERE LOWER(p.firstName) LIKE :term OR LOWER(p.lastName) LIKE :term OR LOWER(p.documentNumber) LIKE :term ORDER BY p.lastName, p.firstName", PatientJpa.class)
                .setParameter("term", term).setMaxResults(50).getResultList().stream().map(this::toDomain).toList();
    }

    private PatientJpa toJpa(Patient patient) {
        PatientJpa jpa = new PatientJpa();
        jpa.setId(patient.id().value()); jpa.setDocumentType(patient.documentType()); jpa.setDocumentNumber(patient.documentNumber());
        jpa.setFirstName(patient.firstName()); jpa.setLastName(patient.lastName()); jpa.setBirthDate(patient.birthDate());
        jpa.setEmail(patient.email()); jpa.setPhone(patient.phone()); jpa.setAddress(patient.address());
        jpa.setAffiliationType(patient.affiliationType()); jpa.setCreatedAt(patient.createdAt());
        jpa.setUserId(patient.accountUserId() == null ? null : patient.accountUserId().value());
        return jpa;
    }
    private Patient toDomain(PatientJpa jpa) {
        return new Patient(PatientId.of(jpa.getId()), jpa.getDocumentType(), jpa.getDocumentNumber(), jpa.getFirstName(),
                jpa.getLastName(), jpa.getBirthDate(), jpa.getEmail(), jpa.getPhone(), jpa.getAddress(),
                jpa.getAffiliationType(), jpa.getCreatedAt(), jpa.getUserId() == null ? null : UserId.of(jpa.getUserId()));
    }
}
