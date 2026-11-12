package co.ufps.clinica.application.port.out.billing;

import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.appointment.AppointmentId;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Invoice saveWithLines(Invoice invoice, List<InvoiceLine> lines);
    Optional<Invoice> findById(InvoiceId id);
    Optional<Invoice> findByNumber(String number);
    Optional<Invoice> findByAppointment(AppointmentId appointmentId);
    List<Invoice> findByPatient(PatientId patientId);
    List<InvoiceLine> findByInvoice(InvoiceId invoiceId);
}
