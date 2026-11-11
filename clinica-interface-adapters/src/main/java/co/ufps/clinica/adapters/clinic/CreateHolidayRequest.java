package co.ufps.clinica.adapters.clinic;

import java.time.LocalDate;

public record CreateHolidayRequest(String branchId, LocalDate date, String description) {}