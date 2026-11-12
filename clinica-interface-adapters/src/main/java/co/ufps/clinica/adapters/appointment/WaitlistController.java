package co.ufps.clinica.adapters.appointment;

import co.ufps.clinica.application.appointment.JoinWaitlistCommand;
import co.ufps.clinica.application.appointment.WaitlistService;
import co.ufps.clinica.domain.appointment.WaitlistEntry;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class WaitlistController {
    private final WaitlistService service;
    public WaitlistController(WaitlistService service) { this.service = Objects.requireNonNull(service); }
    public WaitlistResponse join(UserId userId, JoinWaitlistRequest request) {
        DoctorId doctor = blank(request.doctorId()) ? null : DoctorId.of(UUID.fromString(request.doctorId()));
        SpecialtyId specialty = blank(request.specialtyId()) ? null : SpecialtyId.of(UUID.fromString(request.specialtyId()));
        return present(service.join(userId, new JoinWaitlistCommand(doctor, specialty, request.requestedFrom(), request.requestedUntil())));
    }
    public List<WaitlistResponse> mine(UserId userId) { return service.mine(userId).stream().map(this::present).toList(); }
    public WaitlistResponse cancel(UserId userId, String id) { return present(service.cancel(userId, WaitlistEntryId.of(UUID.fromString(id)))); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private WaitlistResponse present(WaitlistEntry entry) {
        return new WaitlistResponse(entry.id().value().toString(), entry.doctorId() == null ? null : entry.doctorId().value().toString(),
                entry.specialtyId() == null ? null : entry.specialtyId().value().toString(), entry.requestedFrom(),
                entry.requestedUntil(), entry.createdAt(), entry.status().name());
    }
}
