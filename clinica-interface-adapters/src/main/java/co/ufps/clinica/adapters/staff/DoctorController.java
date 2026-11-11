package co.ufps.clinica.adapters.staff;

import co.ufps.clinica.application.staff.*;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.*;
import co.ufps.clinica.domain.clinic.BranchId;
import java.util.List;
import java.util.Objects;

public final class DoctorController {
    private final DoctorService doctorService;
    public DoctorController(DoctorService doctorService) { this.doctorService = Objects.requireNonNull(doctorService); }

    public DoctorResponse registerDoctor(RegisterDoctorRequest request) {
        Doctor d = doctorService.registerDoctor(new RegisterDoctorCommand(
                UserId.of(request.userId()), request.licenseNumber(), request.firstName(),
                request.lastName(), SpecialtyId.of(request.specialtyId())));
        return presentDoctor(d);
    }

    public DoctorResponse updateStatus(String id, String status) {
        Doctor d = doctorService.updateStatus(DoctorId.of(id), StaffStatus.valueOf(status));
        return presentDoctor(d);
    }

    public List<DoctorResponse> getAllActiveDoctors() {
        return doctorService.getAllActiveDoctors().stream().map(this::presentDoctor).toList();
    }

    public List<DoctorResponse> getDoctorsBySpecialty(String specialtyId) {
        return doctorService.getDoctorsBySpecialty(SpecialtyId.of(specialtyId)).stream().map(this::presentDoctor).toList();
    }

    public AvailabilityResponse setAvailability(SetAvailabilityRequest request) {
        DoctorAvailability a = doctorService.setAvailability(new SetDoctorAvailabilityCommand(
                DoctorId.of(request.doctorId()), BranchId.of(request.branchId()),
                java.time.DayOfWeek.valueOf(request.day()), request.startsAt(), request.endsAt(),
                request.validFrom(), request.validUntil()));
        return presentAvailability(a);
    }

    public List<AvailabilityResponse> getDoctorAvailability(String doctorId) {
        return doctorService.getDoctorAvailability(DoctorId.of(doctorId)).stream().map(this::presentAvailability).toList();
    }

    private DoctorResponse presentDoctor(Doctor d) {
        return new DoctorResponse(d.id().value().toString(), d.userId().value().toString(),
                d.licenseNumber(), d.firstName(), d.lastName(),
                d.specialtyId().value().toString(), d.status().name());
    }

    private AvailabilityResponse presentAvailability(DoctorAvailability a) {
        return new AvailabilityResponse(a.id().value().toString(), a.doctorId().value().toString(),
                a.branchId().value().toString(), a.day().name(),
                a.startsAt(), a.endsAt(), a.validFrom(), a.validUntil());
    }
}