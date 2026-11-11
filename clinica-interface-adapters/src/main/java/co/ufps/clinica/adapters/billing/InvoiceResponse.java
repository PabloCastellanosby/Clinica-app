package co.ufps.clinica.adapters.billing;

import java.time.Instant;

public record InvoiceResponse(String id, String number, String patientId, String appointmentId,
                              Instant issuedAt, long subtotal, long tax, long total,
                              String currency, String status) {}