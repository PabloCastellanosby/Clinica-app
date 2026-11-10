package co.ufps.clinica.domain.clinic;
import java.time.LocalDate;
public record Holiday(HolidayId id, BranchId branchId, LocalDate date, String description) {}
