package co.ufps.clinica.domain.staff;
import co.ufps.clinica.domain.clinic.BranchId;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
public record DoctorAvailability(DoctorAvailabilityId id, DoctorId doctorId, BranchId branchId, DayOfWeek day,
                                 LocalTime startsAt, LocalTime endsAt, LocalDate validFrom, LocalDate validUntil) {}
