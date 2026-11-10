package co.ufps.clinica.domain.clinic;
import java.time.DayOfWeek;
import java.time.LocalTime;
public record OpeningHours(OpeningHoursId id, BranchId branchId, DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {}
