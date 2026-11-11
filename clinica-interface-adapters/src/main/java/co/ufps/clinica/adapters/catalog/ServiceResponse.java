package co.ufps.clinica.adapters.catalog;

public record ServiceResponse(String id, String specialtyId, String name, String description,
                              long priceMinorUnits, String currency, boolean active) {}