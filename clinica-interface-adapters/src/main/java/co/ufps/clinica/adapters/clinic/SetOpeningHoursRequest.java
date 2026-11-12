package co.ufps.clinica.adapters.clinic;

import java.time.LocalTime;

public record SetOpeningHoursRequest(String branchId, String day, LocalTime opensAt, LocalTime closesAt) {}
