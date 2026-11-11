package co.ufps.clinica.application.billing;

import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import java.time.Instant;
import java.util.*;

public final class InvoiceService {
    private final InvoiceRepository repository;
    public InvoiceService(InvoiceRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Invoice createInvoice(CreateInvoiceCommand command) {
        long subtotal = command.lines().stream().mapToLong(l -> l.quantity() * l.unitPrice()).sum();
        long tax = subtotal * command.taxRate() / 100;
        long total = subtotal + tax;
        String number = "INV-" + Instant.now().getEpochSecond();
        Invoice invoice = new Invoice(InvoiceId.generate(), command.patientId(), command.appointmentId(),
                number, Instant.now(), subtotal, tax, total, command.currency(), InvoiceStatus.PENDING);
        Invoice saved = repository.save(invoice);
        for (InvoiceLine line : command.lines()) {
            InvoiceLine il = new InvoiceLine(InvoiceLineId.generate(), saved.id(),
                    line.description(), line.quantity(), line.unitPrice(), (long) line.quantity() * line.unitPrice());
            repository.saveLine(il);
        }
        return saved;
    }

    public Invoice changeStatus(InvoiceId id, InvoiceStatus status) {
        Invoice current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Factura no encontrada"));
        Invoice updated = new Invoice(current.id(), current.patientId(), current.appointmentId(),
                current.number(), current.issuedAt(), current.subtotal(), current.tax(), current.total(),
                current.currency(), status);
        return repository.save(updated);
    }

    public Optional<Invoice> findById(InvoiceId id) {
        return repository.findById(id);
    }

    public List<Invoice> findByPatient(PatientId patientId) {
        return repository.findByPatient(patientId);
    }
}
