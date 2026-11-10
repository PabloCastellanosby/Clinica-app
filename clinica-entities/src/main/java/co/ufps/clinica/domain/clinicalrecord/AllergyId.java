package co.ufps.clinica.domain.clinicalrecord;

import java.util.Objects;
import java.util.UUID;

/** Identificador tipado para Allergy. */
public record AllergyId(UUID value) {
    public AllergyId {
        Objects.requireNonNull(value, "AllergyId no puede ser nulo");
    }

    public static AllergyId generate() {
        return new AllergyId(UUID.randomUUID());
    }

    public static AllergyId of(UUID value) {
        return new AllergyId(value);
    }
}