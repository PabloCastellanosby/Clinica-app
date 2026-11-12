package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.patient.PatientId;

/** Política de acceso clínico coherente con las reglas del servicio de historias. */
public final class ClinicalAccessPolicy {
    public boolean canRead(PatientId requesterPatientId, Role role, PatientId patientId, boolean assignedToCare) {
        return switch (role) {
            case MEDICO -> assignedToCare;
            case PACIENTE -> requesterPatientId != null && requesterPatientId.equals(patientId);
            case ADMINISTRADOR -> true;
            case RECEPCION, FARMACIA -> false;
        };
    }
    public boolean canWrite(Role role, boolean assignedToCare) {
        return role == Role.ADMINISTRADOR || role == Role.MEDICO && assignedToCare;
    }
}
