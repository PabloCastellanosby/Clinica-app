package co.ufps.clinica.adapters.clinic;

public record BranchResponse(String id, String clinicId, String name, String address, String phone, String timeZone, boolean active) {}