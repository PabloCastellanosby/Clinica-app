package co.ufps.clinica.adapters.clinic;

import java.time.LocalDate;

public record HolidayResponse(String id, String branchId, LocalDate date, String description) {}