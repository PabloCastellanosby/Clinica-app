package co.ufps.clinica.application;

import co.ufps.clinica.domain.Role;
import java.util.UUID;

/** Regla de autorización por defecto cerrada para historias clínicas. */
public final class ClinicalAccessPolicy {
    public boolean canRead(UUID requesterId, Role role, UUID patientId, boolean assignedToCare) {
        return switch (role) {
            case MEDICO -> assignedToCare;
            case PACIENTE -> requesterId.equals(patientId);
            case ADMINISTRADOR, RECEPCION, FARMACIA -> false;
        };
    }
    public boolean canWrite(Role role, boolean assignedToCare) { return role == Role.MEDICO && assignedToCare; }
}
