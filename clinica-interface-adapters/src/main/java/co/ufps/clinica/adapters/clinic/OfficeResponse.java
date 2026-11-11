package co.ufps.clinica.adapters.clinic;

public record OfficeResponse(String id, String branchId, String name, String type, int capacity, boolean active) {}