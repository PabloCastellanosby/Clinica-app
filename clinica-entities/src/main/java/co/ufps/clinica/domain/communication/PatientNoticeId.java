package co.ufps.clinica.domain.communication;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para PatientNotice. */
public record PatientNoticeId(UUID value) {
    public PatientNoticeId {
        Objects.requireNonNull(value, "PatientNoticeId no puede ser nulo");
    }

    public static PatientNoticeId generate() {
        return new PatientNoticeId(UUID.randomUUID());
    }

    public static PatientNoticeId of(UUID value) {
        return new PatientNoticeId(value);
    }
}