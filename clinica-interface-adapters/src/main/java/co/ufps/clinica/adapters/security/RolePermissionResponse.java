package co.ufps.clinica.adapters.security;

public record RolePermissionResponse(String role, String permissionKey, String description, boolean allowed) {}
