package co.ufps.clinica.domain.appointment;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
public record WaitlistEntry(WaitlistEntryId id, PatientId patientId, DoctorId doctorId, SpecialtyId specialtyId,
                            Instant requestedFrom, Instant requestedUntil, Instant createdAt, WaitlistStatus status) {
    public WaitlistEntry {
        if (id == null || patientId == null || requestedFrom == null || requestedUntil == null || createdAt == null || status == null)
            throw new IllegalArgumentException("La solicitud de lista de espera está incompleta");
        if ((doctorId == null) == (specialtyId == null))
            throw new IllegalArgumentException("Debe especificar un médico o una especialidad");
        if (!requestedFrom.isBefore(requestedUntil)) throw new IllegalArgumentException("El rango de disponibilidad no es válido");
    }
    public WaitlistEntry withStatus(WaitlistStatus next) {
        boolean allowed = switch (status) {
            case WAITING -> next == WaitlistStatus.OFFERED || next == WaitlistStatus.CANCELLED || next == WaitlistStatus.EXPIRED;
            case OFFERED -> next == WaitlistStatus.BOOKED || next == WaitlistStatus.CANCELLED || next == WaitlistStatus.EXPIRED;
            case BOOKED, CANCELLED, EXPIRED -> false;
        };
        if (!allowed) throw new IllegalStateException("Transición de lista de espera no permitida");
        return new WaitlistEntry(id, patientId, doctorId, specialtyId, requestedFrom, requestedUntil, createdAt, next);
    }
}
