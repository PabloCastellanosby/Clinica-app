package co.ufps.clinica.domain;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Attachment. */
public record AttachmentId(UUID value) {
    public AttachmentId {
        Objects.requireNonNull(value, "AttachmentId no puede ser nulo");
    }

    public static AttachmentId generate() {
        return new AttachmentId(UUID.randomUUID());
    }

    public static AttachmentId of(UUID value) {
        return new AttachmentId(value);
    }
}