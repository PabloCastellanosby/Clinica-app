package co.ufps.clinica.domain.staff;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
public record DoctorAvailability(DoctorAvailabilityId id, DoctorId doctorId, BranchId branchId, DayOfWeek day,
                                 LocalTime startsAt, LocalTime endsAt, LocalDate validFrom, LocalDate validUntil) {
    public DoctorAvailability {
        if (id == null || doctorId == null || branchId == null || day == null || startsAt == null || endsAt == null
                || validFrom == null || !startsAt.isBefore(endsAt)
                || (validUntil != null && validUntil.isBefore(validFrom)))
            throw new IllegalArgumentException("La disponibilidad médica debe tener relaciones y un intervalo válido");
    }
}
