package co.ufps.clinica.application.catalog;

import co.ufps.clinica.domain.catalog.SpecialtyId;

public record CreateServiceCommand(SpecialtyId specialtyId, String name, String description,
                                   long priceMinorUnits, String currency) {}
