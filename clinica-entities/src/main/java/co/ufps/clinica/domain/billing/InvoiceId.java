package co.ufps.clinica.domain.billing;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Invoice. */
public record InvoiceId(UUID value) {
    public InvoiceId {
        Objects.requireNonNull(value, "InvoiceId no puede ser nulo");
    }

    public static InvoiceId generate() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId of(UUID value) {
        return new InvoiceId(value);
    }
}