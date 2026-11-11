package co.ufps.clinica.adapters.clinic;

public record RegisterBranchRequest(String clinicId, String name, String address, String phone, String timeZone) {}