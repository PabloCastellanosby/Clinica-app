package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para InvoiceLine. */
public record InvoiceLineId(UUID value) {
    public InvoiceLineId {
        Objects.requireNonNull(value, "InvoiceLineId no puede ser nulo");
    }

    public static InvoiceLineId generate() {
        return new InvoiceLineId(UUID.randomUUID());
    }

    public static InvoiceLineId of(UUID value) {
        return new InvoiceLineId(value);
    }
}