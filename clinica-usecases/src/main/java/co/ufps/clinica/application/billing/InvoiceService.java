package co.ufps.clinica.application.billing;

import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import java.time.Instant;
import java.util.*;

public final class InvoiceService {
    private final InvoiceRepository repository;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    public InvoiceService(InvoiceRepository repository, PatientRepository patients, AppointmentRepository appointments) {
        this.repository = Objects.requireNonNull(repository);
        this.patients = Objects.requireNonNull(patients);
        this.appointments = Objects.requireNonNull(appointments);
    }

    public Invoice createInvoice(CreateInvoiceCommand command) {
        Objects.requireNonNull(command, "La factura es obligatoria");
        patients.findById(command.patientId()).orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
        if (command.appointmentId() != null) {
            if (repository.findByAppointment(command.appointmentId()).isPresent())
                throw new IllegalStateException("La cita ya tiene una factura asociada");
            var appointment = appointments.findById(command.appointmentId())
                    .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));
            if (!appointment.patientId().equals(command.patientId()))
                throw new IllegalArgumentException("La cita pertenece a otro paciente");
        }
        Instant now = Instant.now();
        InvoiceId id = InvoiceId.generate();
        long subtotal = 0;
        List<InvoiceLine> lines = new ArrayList<>();
        for (CreateInvoiceCommand.InvoiceLineDto line : command.lines()) {
            if (line == null || line.description() == null || line.description().isBlank()
                    || line.quantity() <= 0 || line.unitPrice() < 0) {
                throw new IllegalArgumentException("Cada línea requiere descripción, cantidad positiva y precio no negativo");
            }
            long lineTotal = Math.multiplyExact((long) line.quantity(), line.unitPrice());
            subtotal = Math.addExact(subtotal, lineTotal);
            lines.add(new InvoiceLine(InvoiceLineId.generate(), id, line.description().trim(),
                    line.quantity(), line.unitPrice(), lineTotal));
        }
        long tax = Math.multiplyExact(subtotal, command.taxRate()) / 100;
        long total = Math.addExact(subtotal, tax);
        Invoice invoice = new Invoice(id, command.patientId(), command.appointmentId(),
                "INV-" + id.value(), now, subtotal, tax, total, command.currency(), InvoiceStatus.PENDING);
        return repository.saveWithLines(invoice, lines);
    }

    public Invoice changeStatus(InvoiceId id, InvoiceStatus status) {
        Invoice current = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Factura no encontrada"));
        if (!current.status().canTransitionTo(status)) throw new IllegalStateException("Transición de factura no permitida");
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
