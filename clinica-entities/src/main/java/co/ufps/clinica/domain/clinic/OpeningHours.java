package co.ufps.clinica.domain.clinic;
import java.time.DayOfWeek;
import java.time.LocalTime;
public record OpeningHours(OpeningHoursId id, BranchId branchId, DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
    public OpeningHours {
        if (id == null || branchId == null || day == null || opensAt == null || closesAt == null || !opensAt.isBefore(closesAt))
            throw new IllegalArgumentException("El horario debe tener sede, día y un intervalo válido");
    }
}
