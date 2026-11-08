package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Branch. */
public record BranchId(UUID value) {
    public BranchId {
        Objects.requireNonNull(value, "BranchId no puede ser nulo");
    }

    public static BranchId generate() {
        return new BranchId(UUID.randomUUID());
    }

    public static BranchId of(UUID value) {
        return new BranchId(value);
    }
}