package co.ufps.clinica.domain.catalog;
public record Service(ServiceId id, SpecialtyId specialtyId, String name, String description, long priceMinorUnits, String currency, boolean active) {}
