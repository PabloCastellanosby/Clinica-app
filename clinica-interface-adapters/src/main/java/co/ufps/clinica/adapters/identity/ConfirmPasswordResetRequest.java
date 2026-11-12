package co.ufps.clinica.adapters.identity;

public record ConfirmPasswordResetRequest(String token, String newPassword) {}
