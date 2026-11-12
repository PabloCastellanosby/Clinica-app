package co.ufps.clinica.adapters.appointment;

import co.ufps.clinica.application.appointment.AppointmentScheduler;
import co.ufps.clinica.application.appointment.ScheduleAppointmentCommand;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.appointment.AppointmentStatus;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import co.ufps.clinica.domain.catalog.ServiceId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class AppointmentController {
    private final AppointmentScheduler scheduler;
    public AppointmentController(AppointmentScheduler scheduler) { this.scheduler = Objects.requireNonNull(scheduler); }

    public AppointmentResponse scheduleForPatient(UserId userId, ScheduleAppointmentRequest request) {
        return present(scheduler.scheduleForUser(userId, command(null, request, false)));
    }
    public AppointmentResponse scheduleForReception(ScheduleAppointmentRequest request) {
        return present(scheduler.schedule(command(request.patientId(), request, true)));
    }
    public AppointmentResponse bookWaitlistOffer(UserId userId, String entryId, ScheduleAppointmentRequest request) {
        return present(scheduler.bookWaitlistOffer(userId, WaitlistEntryId.of(UUID.fromString(entryId)), command(null, request, false)));
    }
    public AppointmentResponse changeStatus(String id, String status, String reason) {
        return present(scheduler.changeStatus(AppointmentId.of(UUID.fromString(id)), AppointmentStatus.valueOf(status), reason));
    }
    public AppointmentResponse changeStatusByDoctor(UserId userId, String id, String status) {
        return present(scheduler.changeStatusByDoctor(userId, AppointmentId.of(UUID.fromString(id)), AppointmentStatus.valueOf(status)));
    }
    public AppointmentResponse cancelForPatient(UserId userId, String id, String reason) {
        return present(scheduler.cancelForPatient(userId, AppointmentId.of(UUID.fromString(id)), reason));
    }
    public List<AppointmentResponse> patientAppointments(UserId userId, Instant from, Instant until) {
        return scheduler.forPatientUser(userId, from, until).stream().map(this::present).toList();
    }
    public List<AppointmentResponse> doctorAgenda(DoctorId doctorId, Instant from, Instant until) {
        return scheduler.forDoctor(doctorId, from, until).stream().map(this::present).toList();
    }
    public List<AppointmentResponse> doctorAgenda(UserId userId, Instant from, Instant until) {
        return scheduler.forDoctorUser(userId, from, until).stream().map(this::present).toList();
    }

    private ScheduleAppointmentCommand command(String patientId, ScheduleAppointmentRequest request, boolean patientRequired) {
        String selectedPatient = patientId == null ? request.patientId() : patientId;
        if (patientRequired && (selectedPatient == null || selectedPatient.isBlank())) throw new IllegalArgumentException("El identificador del paciente es obligatorio");
        PatientId parsedPatientId = selectedPatient == null || selectedPatient.isBlank()
                ? PatientId.generate() : PatientId.of(UUID.fromString(selectedPatient));
        return new ScheduleAppointmentCommand(parsedPatientId,
                DoctorId.of(UUID.fromString(request.doctorId())), BranchId.of(UUID.fromString(request.branchId())),
                OfficeId.of(UUID.fromString(request.officeId())), request.serviceId() == null || request.serviceId().isBlank()
                ? null : ServiceId.of(UUID.fromString(request.serviceId())), request.startsAt(), request.endsAt());
    }
    private AppointmentResponse present(Appointment appointment) {
        return new AppointmentResponse(appointment.id().value().toString(), appointment.patientId().value().toString(),
                appointment.doctorId().value().toString(), appointment.branchId().value().toString(),
                appointment.officeId().value().toString(), appointment.serviceId() == null ? null : appointment.serviceId().value().toString(),
                appointment.startsAt(), appointment.endsAt(), appointment.status().name(), appointment.cancellationReason());
    }
}
