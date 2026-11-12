package co.ufps.clinica.application.port.out.staff;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.*;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(DoctorId id);
    Optional<Doctor> findByUserId(UserId userId);
    List<Doctor> findAllActive();
    List<Doctor> findBySpecialty(SpecialtyId specialtyId);
    DoctorAvailability saveAvailability(DoctorAvailability availability);
    List<DoctorAvailability> findByDoctor(DoctorId doctorId);
}
