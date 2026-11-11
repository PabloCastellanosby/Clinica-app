package co.ufps.clinica.frameworks.persistence.billing;

import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InvoiceRepositoryJpa implements InvoiceRepository {
    @PersistenceContext EntityManager entityManager;

    @Override @Transactional public Invoice save(Invoice invoice) {
        entityManager.merge(toJpa(invoice));
        return invoice;
    }

    @Override public Optional<Invoice> findById(InvoiceId id) {
        InvoiceJpa jpa = entityManager.find(InvoiceJpa.class, id.value());
        return Optional.ofNullable(jpa).map(this::toDomain);
    }

    @Override public List<Invoice> findByPatient(PatientId patientId) {
        return entityManager.createQuery("SELECT i FROM InvoiceJpa i WHERE i.patientId = :pid", InvoiceJpa.class)
                .setParameter("pid", patientId.value()).getResultList().stream().map(this::toDomain).toList();
    }

    @Override @Transactional public InvoiceLine saveLine(InvoiceLine line) {
        entityManager.merge(toJpa(line));
        return line;
    }

    private InvoiceJpa toJpa(Invoice inv) {
        InvoiceJpa jpa = new InvoiceJpa();
        jpa.setId(inv.id().value()); jpa.setPatientId(inv.patientId().value());
        jpa.setAppointmentId(inv.appointmentId() == null ? null : inv.appointmentId().value());
        jpa.setNumber(inv.number()); jpa.setIssuedAt(inv.issuedAt());
        jpa.setSubtotal(inv.subtotal()); jpa.setTax(inv.tax()); jpa.setTotal(inv.total());
        jpa.setCurrency(inv.currency()); jpa.setStatus(inv.status().name());
        return jpa;
    }
    private Invoice toDomain(InvoiceJpa jpa) {
        return new Invoice(InvoiceId.of(jpa.getId()), PatientId.of(jpa.getPatientId()),
                jpa.getAppointmentId() == null ? null : AppointmentId.of(jpa.getAppointmentId()),
                jpa.getNumber(), jpa.getIssuedAt(), jpa.getSubtotal(), jpa.getTax(),
                jpa.getTotal(), jpa.getCurrency(), InvoiceStatus.valueOf(jpa.getStatus()));
    }

    private InvoiceLineJpa toJpa(InvoiceLine line) {
        InvoiceLineJpa jpa = new InvoiceLineJpa();
        jpa.setId(line.id().value()); jpa.setInvoiceId(line.invoiceId().value());
        jpa.setDescription(line.description()); jpa.setQuantity(line.quantity());
        jpa.setUnitPrice(line.unitPrice()); jpa.setTotal(line.total());
        return jpa;
    }
}
