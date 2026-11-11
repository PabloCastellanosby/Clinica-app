package co.ufps.clinica.application.port.out.catalog;

import co.ufps.clinica.domain.catalog.*;
import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    Specialty saveSpecialty(Specialty specialty);
    Optional<Specialty> findSpecialtyById(SpecialtyId id);
    List<Specialty> findAllActiveSpecialties();
    Service saveService(Service service);
    Optional<Service> findServiceById(ServiceId id);
    List<Service> findAllActiveServices();
    List<Service> findServicesBySpecialty(SpecialtyId specialtyId);
}
