package co.ufps.clinica.domain.billing;
public record InvoiceLine(InvoiceLineId id, InvoiceId invoiceId, String description, int quantity,
                          long unitPrice, long total) {}
