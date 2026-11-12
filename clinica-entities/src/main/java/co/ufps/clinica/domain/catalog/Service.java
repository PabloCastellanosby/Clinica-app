package co.ufps.clinica.domain.catalog;
public record Service(ServiceId id, SpecialtyId specialtyId, String name, String description, long priceMinorUnits, String currency, boolean active) {
    public Service {
        if (id == null || specialtyId == null || name == null || name.isBlank() || priceMinorUnits < 0
                || currency == null || !currency.matches("[A-Z]{3}"))
            throw new IllegalArgumentException("El servicio debe tener nombre, precio no negativo y moneda ISO válida");
    }
}
