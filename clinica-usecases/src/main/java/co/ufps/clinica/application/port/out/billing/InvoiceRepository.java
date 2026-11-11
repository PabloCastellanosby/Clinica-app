package co.ufps.clinica.application.port.out.billing;

import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.patient.PatientId;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    InvoiceLine saveLine(InvoiceLine line);
    Optional<Invoice> findById(InvoiceId id);
    Optional<Invoice> findByNumber(String number);
    List<Invoice> findByPatient(PatientId patientId);
    List<InvoiceLine> findByInvoice(InvoiceId invoiceId);
}
