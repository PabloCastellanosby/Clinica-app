package co.ufps.clinica.application.clinic;

import co.ufps.clinica.domain.clinic.BranchId;
import java.time.LocalDate;

public record CreateHolidayCommand(BranchId branchId, LocalDate date, String description) {}
