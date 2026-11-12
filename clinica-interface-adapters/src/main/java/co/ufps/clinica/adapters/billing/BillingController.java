package co.ufps.clinica.adapters.billing;

import co.ufps.clinica.application.billing.*;
import co.ufps.clinica.domain.billing.*;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import java.util.List;
import java.util.Objects;

public final class BillingController {
    private final InvoiceService invoiceService;
    public BillingController(InvoiceService invoiceService) { this.invoiceService = Objects.requireNonNull(invoiceService); }

    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        var command = new CreateInvoiceCommand(
                PatientId.of(java.util.UUID.fromString(request.patientId())),
                AppointmentId.of(java.util.UUID.fromString(request.appointmentId())),
                request.lines().stream().map(l -> new CreateInvoiceCommand.InvoiceLineDto(l.description(), l.quantity(), l.unitPrice())).toList(),
                request.taxRate(), request.currency());
        Invoice invoice = invoiceService.createInvoice(command);
        return present(invoice);
    }

    public InvoiceResponse changeStatus(String id, String status) {
        Invoice updated = invoiceService.changeStatus(InvoiceId.of(java.util.UUID.fromString(id)), InvoiceStatus.valueOf(status));
        return present(updated);
    }

    public InvoiceResponse findById(String id) {
        return invoiceService.findById(InvoiceId.of(java.util.UUID.fromString(id))).map(this::present)
                .orElseThrow(() -> new java.util.NoSuchElementException("Factura no encontrada"));
    }

    public List<InvoiceResponse> findByPatient(String patientId) {
        return invoiceService.findByPatient(PatientId.of(java.util.UUID.fromString(patientId))).stream().map(this::present).toList();
    }

    private InvoiceResponse present(Invoice i) {
        return new InvoiceResponse(i.id().value().toString(), i.number(), i.patientId().value().toString(),
                i.appointmentId().value().toString(), i.issuedAt(), i.subtotal(), i.tax(), i.total(),
                i.currency(), i.status().name());
    }
}
