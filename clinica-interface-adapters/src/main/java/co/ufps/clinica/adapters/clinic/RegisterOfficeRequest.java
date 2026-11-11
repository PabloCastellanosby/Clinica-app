package co.ufps.clinica.adapters.clinic;

public record RegisterOfficeRequest(String branchId, String name, String type, int capacity) {}