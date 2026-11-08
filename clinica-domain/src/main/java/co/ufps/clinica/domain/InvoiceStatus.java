package co.ufps.clinica.domain;

/** Estados de pago y emisión de facturas. */
public enum InvoiceStatus {
    PENDING,
    PAID,
    PARTIALLY_PAID,
    CANCELLED,
    REFUNDED
}
