package co.ufps.clinica.adapters.staff;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityResponse(String id, String doctorId, String branchId, String day,
                                   LocalTime startsAt, LocalTime endsAt,
                                   LocalDate validFrom, LocalDate validUntil) {}