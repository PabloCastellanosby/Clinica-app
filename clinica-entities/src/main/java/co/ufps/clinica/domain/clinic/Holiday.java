package co.ufps.clinica.domain.clinic;
import java.time.LocalDate;
public record Holiday(HolidayId id, BranchId branchId, LocalDate date, String description) {
    public Holiday {
        if (id == null || branchId == null || date == null || description == null || description.isBlank())
            throw new IllegalArgumentException("El festivo debe tener sede, fecha y descripción");
    }
}
