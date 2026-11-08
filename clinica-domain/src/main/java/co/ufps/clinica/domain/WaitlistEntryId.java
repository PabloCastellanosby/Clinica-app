package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para WaitlistEntry. */
public record WaitlistEntryId(UUID value) {
    public WaitlistEntryId {
        Objects.requireNonNull(value, "WaitlistEntryId no puede ser nulo");
    }

    public static WaitlistEntryId generate() {
        return new WaitlistEntryId(UUID.randomUUID());
    }

    public static WaitlistEntryId of(UUID value) {
        return new WaitlistEntryId(value);
    }
}