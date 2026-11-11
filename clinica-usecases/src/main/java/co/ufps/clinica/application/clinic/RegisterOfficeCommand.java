package co.ufps.clinica.application.clinic;

import co.ufps.clinica.domain.clinic.BranchId;

public record RegisterOfficeCommand(BranchId branchId, String name, String type, int capacity) {}
