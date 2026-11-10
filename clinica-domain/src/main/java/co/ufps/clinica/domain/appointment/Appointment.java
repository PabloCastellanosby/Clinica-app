package co.ufps.clinica.domain.appointment;

import co.ufps.clinica.domain.catalog.ServiceId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;

public record Appointment(AppointmentId id, PatientId patientId, DoctorId doctorId, BranchId branchId, OfficeId officeId,
                          ServiceId serviceId, Instant startsAt, Instant endsAt, AppointmentStatus status,
                          String cancellationReason, Instant createdAt) {
    public Appointment {
        if (startsAt == null || endsAt == null || !startsAt.isBefore(endsAt)) throw new IllegalArgumentException("La cita debe tener duración positiva");
    }
    public static Appointment schedule(PatientId patientId, DoctorId doctorId, BranchId branchId, OfficeId officeId,
                                       ServiceId serviceId, Instant startsAt, Instant endsAt, Instant createdAt) {
        return new Appointment(AppointmentId.generate(), patientId, doctorId, branchId, officeId, serviceId,
                startsAt, endsAt, AppointmentStatus.REQUESTED, null, createdAt);
    }
    public Appointment withStatus(AppointmentStatus next) {
        if (status == null || next == null || !status.canTransitionTo(next)) throw new IllegalStateException("Transición de cita no permitida: " + status + " -> " + next);
        return new Appointment(id, patientId, doctorId, branchId, officeId, serviceId, startsAt, endsAt, next, cancellationReason, createdAt);
    }
}
