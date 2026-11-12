package co.ufps.clinica.adapters.identity;

public record RegisterAccountRequest(String email, String password, String documentType, String documentNumber) {}
