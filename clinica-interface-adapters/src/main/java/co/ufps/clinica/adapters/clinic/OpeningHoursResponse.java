package co.ufps.clinica.adapters.clinic;

import java.time.LocalTime;

public record OpeningHoursResponse(String id, String branchId, String day, LocalTime opensAt, LocalTime closesAt) {}
