package co.ufps.clinica.application;

import co.ufps.clinica.domain.Role;
import co.ufps.clinica.domain.PatientId;

/** Regla de autorización por defecto cerrada para historias clínicas. */
public final class ClinicalAccessPolicy {
    public boolean canRead(PatientId requesterPatientId, Role role, PatientId patientId, boolean assignedToCare) {
        return switch (role) {
            case MEDICO -> assignedToCare;
            case PACIENTE -> requesterPatientId != null && requesterPatientId.equals(patientId);
            case ADMINISTRADOR, RECEPCION, FARMACIA -> false;
        };
    }
    public boolean canWrite(Role role, boolean assignedToCare) { return role == Role.MEDICO && assignedToCare; }
}
