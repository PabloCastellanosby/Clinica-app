package co.ufps.clinica.adapters.billing;

import java.util.List;

public record CreateInvoiceRequest(String patientId, String appointmentId,
                                   List<InvoiceLineDto> lines, int taxRate, String currency) {
    public record InvoiceLineDto(String description, int quantity, long unitPrice) {}
}