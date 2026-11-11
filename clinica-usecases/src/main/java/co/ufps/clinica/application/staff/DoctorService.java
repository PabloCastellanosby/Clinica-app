package co.ufps.clinica.application.staff;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.*;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import java.util.*;

public final class DoctorService {
    private final DoctorRepository repository;
    public DoctorService(DoctorRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Doctor registerDoctor(RegisterDoctorCommand command) {
        Doctor doctor = new Doctor(DoctorId.generate(), command.userId(), command.licenseNumber(),
                command.firstName(), command.lastName(), command.specialtyId(), StaffStatus.ACTIVE);
        return repository.save(doctor);
    }

    public Doctor updateStatus(DoctorId id, StaffStatus status) {
        Doctor current = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor no encontrado"));
        Doctor updated = new Doctor(current.id(), current.userId(), current.licenseNumber(),
                current.firstName(), current.lastName(), current.specialtyId(), status);
        return repository.save(updated);
    }

    public List<Doctor> getAllActiveDoctors() {
        return repository.findAllActive();
    }

    public List<Doctor> getDoctorsBySpecialty(SpecialtyId specialtyId) {
        return repository.findBySpecialty(specialtyId);
    }

    public DoctorAvailability setAvailability(SetDoctorAvailabilityCommand command) {
        DoctorAvailability availability = new DoctorAvailability(DoctorAvailabilityId.generate(),
                command.doctorId(), command.branchId(), command.day(), command.startsAt(),
                command.endsAt(), command.validFrom(), command.validUntil());
        return repository.saveAvailability(availability);
    }

    public List<DoctorAvailability> getDoctorAvailability(DoctorId doctorId) {
        return repository.findByDoctor(doctorId);
    }
}
