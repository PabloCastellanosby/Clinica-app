package co.ufps.clinica.application.clinic;

import co.ufps.clinica.domain.clinic.*;
import co.ufps.clinica.application.port.out.clinic.ClinicRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public final class ClinicService {
    private final ClinicRepository repository;
    public ClinicService(ClinicRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public Clinic registerClinic(RegisterClinicCommand command) {
        if (command == null || command.name() == null || command.name().isBlank())
            throw new IllegalArgumentException("El nombre de la clínica es obligatorio");
        Clinic clinic = new Clinic(ClinicId.generate(), command.name(), command.taxId(),
                command.email(), command.phone(), true);
        return repository.saveClinic(clinic);
    }

    public List<Clinic> getAllActiveClinics() {
        return repository.findAllActive();
    }

    public Branch registerBranch(RegisterBranchCommand command) {
        if (command == null || command.clinicId() == null)
            throw new IllegalArgumentException("La sede debe estar asociada a una clínica");
        repository.findById(command.clinicId()).filter(Clinic::active)
                .orElseThrow(() -> new NoSuchElementException("Clínica activa no encontrada"));
        Branch branch = new Branch(BranchId.generate(), command.clinicId(), command.name(),
                command.address(), command.phone(), command.timeZone(), true);
        return repository.saveBranch(branch);
    }

    public List<Branch> getBranchesByClinic(ClinicId clinicId) {
        return repository.findBranchesByClinic(clinicId);
    }

    public Office registerOffice(RegisterOfficeCommand command) {
        if (command == null || command.branchId() == null)
            throw new IllegalArgumentException("El consultorio debe estar asociado a una sede");
        repository.findBranchById(command.branchId()).filter(Branch::active)
                .orElseThrow(() -> new NoSuchElementException("Sede activa no encontrada"));
        if (command.capacity() <= 0) throw new IllegalArgumentException("La capacidad del consultorio debe ser positiva");
        Office office = new Office(OfficeId.generate(), command.branchId(),
                command.name(), command.type(), command.capacity(), true);
        return repository.saveOffice(office);
    }

    public List<Office> getOfficesByBranch(BranchId branchId) {
        return repository.findOfficesByBranch(branchId);
    }

    public Holiday createHoliday(CreateHolidayCommand command) {
        if (command == null || command.branchId() == null)
            throw new IllegalArgumentException("El festivo debe estar asociado a una sede");
        if (command.date() == null || command.description() == null || command.description().isBlank()) throw new IllegalArgumentException("Fecha y descripción del festivo son obligatorias");
        repository.findBranchById(command.branchId()).filter(Branch::active)
                .orElseThrow(() -> new NoSuchElementException("Sede activa no encontrada"));
        Holiday holiday = new Holiday(HolidayId.generate(), command.branchId(),
                command.date(), command.description());
        return repository.saveHoliday(holiday);
    }

    public List<Holiday> getHolidaysByBranch(BranchId branchId) { return repository.findHolidaysByBranch(branchId); }

    public OpeningHours setOpeningHours(BranchId branchId, DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
        if (branchId == null || day == null || opensAt == null || closesAt == null)
            throw new IllegalArgumentException("Sede, día y horas son obligatorios");
        repository.findBranchById(branchId).filter(Branch::active)
                .orElseThrow(() -> new NoSuchElementException("Sede activa no encontrada"));
        if (!opensAt.isBefore(closesAt)) throw new IllegalArgumentException("La hora de apertura debe ser anterior al cierre");
        boolean overlaps = repository.findOpeningHoursByBranch(branchId).stream()
                .filter(existing -> existing.day() == day)
                .anyMatch(existing -> opensAt.isBefore(existing.closesAt()) && closesAt.isAfter(existing.opensAt()));
        if (overlaps) throw new IllegalArgumentException("El intervalo se superpone con otro horario de atención");
        return repository.saveOpeningHours(new OpeningHours(OpeningHoursId.generate(), branchId, day, opensAt, closesAt));
    }

    public List<OpeningHours> getOpeningHours(BranchId branchId) { return repository.findOpeningHoursByBranch(branchId); }
}
