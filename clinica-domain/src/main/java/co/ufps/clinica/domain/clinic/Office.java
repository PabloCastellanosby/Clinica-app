package co.ufps.clinica.domain.clinic;
public record Office(OfficeId id, BranchId branchId, String name, String type, int capacity, boolean active) {}
