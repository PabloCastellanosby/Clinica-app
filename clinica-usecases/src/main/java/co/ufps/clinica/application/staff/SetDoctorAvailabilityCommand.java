package co.ufps.clinica.application.staff;

import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record SetDoctorAvailabilityCommand(DoctorId doctorId, BranchId branchId, DayOfWeek day,
                                           LocalTime startsAt, LocalTime endsAt,
                                           LocalDate validFrom, LocalDate validUntil) {}
