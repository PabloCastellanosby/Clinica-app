package co.ufps.clinica.adapters.clinic;

import co.ufps.clinica.application.clinic.*;
import co.ufps.clinica.domain.clinic.*;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public final class ClinicController {
    private final ClinicService clinicService;
    public ClinicController(ClinicService clinicService) { this.clinicService = Objects.requireNonNull(clinicService); }

    public ClinicResponse registerClinic(RegisterClinicRequest request) {
        Clinic c = clinicService.registerClinic(new RegisterClinicCommand(request.name(), request.taxId(), request.email(), request.phone()));
        return presentClinic(c);
    }

    public List<ClinicResponse> getAllActiveClinics() {
        return clinicService.getAllActiveClinics().stream().map(this::presentClinic).toList();
    }

    public BranchResponse registerBranch(RegisterBranchRequest request) {
        Branch b = clinicService.registerBranch(new RegisterBranchCommand(
                ClinicId.of(request.clinicId()), request.name(), request.address(), request.phone(),
                ZoneId.of(request.timeZone())));
        return presentBranch(b);
    }

    public List<BranchResponse> getBranchesByClinic(String clinicId) {
        return clinicService.getBranchesByClinic(ClinicId.of(clinicId)).stream().map(this::presentBranch).toList();
    }

    public OfficeResponse registerOffice(RegisterOfficeRequest request) {
        Office o = clinicService.registerOffice(new RegisterOfficeCommand(
                BranchId.of(request.branchId()), request.name(), request.type(), request.capacity()));
        return presentOffice(o);
    }

    public List<OfficeResponse> getOfficesByBranch(String branchId) {
        return clinicService.getOfficesByBranch(BranchId.of(branchId)).stream().map(this::presentOffice).toList();
    }

    public HolidayResponse createHoliday(CreateHolidayRequest request) {
        Holiday h = clinicService.createHoliday(new CreateHolidayCommand(
                BranchId.of(request.branchId()), request.date(), request.description()));
        return presentHoliday(h);
    }

    private ClinicResponse presentClinic(Clinic c) {
        return new ClinicResponse(c.id().value().toString(), c.name(), c.taxId(), c.email(), c.phone(), c.isActive());
    }

    private BranchResponse presentBranch(Branch b) {
        return new BranchResponse(b.id().value().toString(), b.clinicId().value().toString(),
                b.name(), b.address(), b.phone(), b.timeZone().toString(), b.isActive());
    }

    private OfficeResponse presentOffice(Office o) {
        return new OfficeResponse(o.id().value().toString(), o.branchId().value().toString(),
                o.name(), o.type(), o.capacity(), o.isActive());
    }

    private HolidayResponse presentHoliday(Holiday h) {
        return new HolidayResponse(h.id().value().toString(), h.branchId().value().toString(),
                h.date(), h.description());
    }
}