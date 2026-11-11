package co.ufps.clinica.adapters.catalog;

import co.ufps.clinica.application.catalog.*;
import co.ufps.clinica.domain.catalog.*;
import java.util.List;
import java.util.Objects;

public final class CatalogController {
    private final CatalogService catalogService;
    public CatalogController(CatalogService catalogService) { this.catalogService = Objects.requireNonNull(catalogService); }

    public SpecialtyResponse createSpecialty(CreateSpecialtyRequest request) {
        Specialty s = catalogService.createSpecialty(new CreateSpecialtyCommand(request.name(), request.description()));
        return presentSpecialty(s);
    }

    public List<SpecialtyResponse> getAllActiveSpecialties() {
        return catalogService.getAllActiveSpecialties().stream().map(this::presentSpecialty).toList();
    }

    public ServiceResponse createService(CreateServiceRequest request) {
        Service s = catalogService.createService(new CreateServiceCommand(
                SpecialtyId.of(request.specialtyId()), request.name(), request.description(),
                request.priceMinorUnits(), request.currency()));
        return presentService(s);
    }

    public List<ServiceResponse> getAllActiveServices() {
        return catalogService.getAllActiveServices().stream().map(this::presentService).toList();
    }

    public List<ServiceResponse> getServicesBySpecialty(String specialtyId) {
        return catalogService.getServicesBySpecialty(SpecialtyId.of(specialtyId)).stream().map(this::presentService).toList();
    }

    private SpecialtyResponse presentSpecialty(Specialty s) {
        return new SpecialtyResponse(s.id().value().toString(), s.name(), s.description(), s.isActive());
    }

    private ServiceResponse presentService(Service s) {
        return new ServiceResponse(s.id().value().toString(), s.specialtyId().value().toString(),
                s.name(), s.description(), s.priceMinorUnits(), s.currency(), s.isActive());
    }
}