package co.ufps.clinica.adapters.catalog;

public record CreateServiceRequest(String specialtyId, String name, String description,
                                   long priceMinorUnits, String currency) {}