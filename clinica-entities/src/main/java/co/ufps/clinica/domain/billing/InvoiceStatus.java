package co.ufps.clinica.domain.billing;

public enum InvoiceStatus {
    PENDING, PARTIALLY_PAID, PAID, CANCELLED, REFUNDED;

    public boolean canTransitionTo(InvoiceStatus next) {
        if (next == null) return false;
        return switch (this) {
            case PENDING -> next == PARTIALLY_PAID || next == PAID || next == CANCELLED;
            case PARTIALLY_PAID -> next == PAID || next == CANCELLED;
            case PAID -> next == REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }
}
