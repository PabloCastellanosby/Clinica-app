package co.ufps.clinica.application.staff;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.*;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import java.util.*;

public final class DoctorService {
    private final DoctorRepository repository;
    private final UserRepository users;
    private final CatalogRepository catalog;
    public DoctorService(DoctorRepository repository, UserRepository users, CatalogRepository catalog) {
        this.repository = Objects.requireNonNull(repository);
        this.users = Objects.requireNonNull(users);
        this.catalog = Objects.requireNonNull(catalog);
    }

    public Doctor registerDoctor(RegisterDoctorCommand command) {
        if (command == null || command.userId() == null || command.specialtyId() == null)
            throw new IllegalArgumentException("Cuenta y especialidad son obligatorias para registrar un médico");
        var user = users.findById(command.userId()).filter(co.ufps.clinica.domain.identity.User::active)
                .orElseThrow(() -> new NoSuchElementException("Cuenta activa no encontrada"));
        if (user.role() != Role.MEDICO) throw new IllegalArgumentException("La cuenta debe tener rol de médico");
        catalog.findSpecialtyById(command.specialtyId()).filter(co.ufps.clinica.domain.catalog.Specialty::active)
                .orElseThrow(() -> new NoSuchElementException("Especialidad activa no encontrada"));
        if (repository.findByUserId(command.userId()).isPresent())
            throw new IllegalArgumentException("La cuenta ya está vinculada a un perfil médico");
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
        if (command == null || command.doctorId() == null)
            throw new IllegalArgumentException("La solicitud de disponibilidad y el médico son obligatorios");
        repository.findById(command.doctorId()).filter(d -> d.status() == StaffStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException("Médico activo no encontrado"));
        DoctorAvailability availability = new DoctorAvailability(DoctorAvailabilityId.generate(),
                command.doctorId(), command.branchId(), command.day(), command.startsAt(),
                command.endsAt(), command.validFrom(), command.validUntil());
        return repository.saveAvailability(availability);
    }

    public List<DoctorAvailability> getDoctorAvailability(DoctorId doctorId) {
        return repository.findByDoctor(doctorId);
    }
}
