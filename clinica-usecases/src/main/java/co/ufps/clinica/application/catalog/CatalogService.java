package co.ufps.clinica.application.catalog;

import co.ufps.clinica.domain.catalog.*;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import java.util.*;

public final class CatalogService {
    private final CatalogRepository repository;
    public CatalogService(CatalogRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Specialty createSpecialty(CreateSpecialtyCommand command) {
        Specialty specialty = new Specialty(SpecialtyId.generate(), command.name(), command.description(), true);
        return repository.saveSpecialty(specialty);
    }

    public List<Specialty> getAllActiveSpecialties() {
        return repository.findAllActiveSpecialties();
    }

    public Service createService(CreateServiceCommand command) {
        Service service = new Service(ServiceId.generate(), command.specialtyId(), command.name(),
                command.description(), command.priceMinorUnits(), command.currency(), true);
        return repository.saveService(service);
    }

    public List<Service> getAllActiveServices() {
        return repository.findAllActiveServices();
    }

    public List<Service> getServicesBySpecialty(SpecialtyId specialtyId) {
        return repository.findServicesBySpecialty(specialtyId);
    }
}
