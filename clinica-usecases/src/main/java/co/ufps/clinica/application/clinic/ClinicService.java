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
        Clinic clinic = new Clinic(ClinicId.generate(), command.name(), command.taxId(),
                command.email(), command.phone(), true);
        return repository.saveClinic(clinic);
    }

    public List<Clinic> getAllActiveClinics() {
        return repository.findAllActive();
    }

    public Branch registerBranch(RegisterBranchCommand command) {
        Branch branch = new Branch(BranchId.generate(), command.clinicId(), command.name(),
                command.address(), command.phone(), command.timeZone(), true);
        return repository.saveBranch(branch);
    }

    public List<Branch> getBranchesByClinic(ClinicId clinicId) {
        return repository.findBranchesByClinic(clinicId);
    }

    public Office registerOffice(RegisterOfficeCommand command) {
        Office office = new Office(OfficeId.generate(), command.branchId(),
                command.name(), command.type(), command.capacity(), true);
        return repository.saveOffice(office);
    }

    public List<Office> getOfficesByBranch(BranchId branchId) {
        return repository.findOfficesByBranch(branchId);
    }

    public Holiday createHoliday(CreateHolidayCommand command) {
        Holiday holiday = new Holiday(HolidayId.generate(), command.branchId(),
                command.date(), command.description());
        return holiday;
    }
}
