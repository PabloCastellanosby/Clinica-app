package co.ufps.clinica.application;

import co.ufps.clinica.domain.*;
import java.time.Instant;

public record ScheduleAppointmentCommand(PatientId patientId, DoctorId doctorId, BranchId branchId,
                                         OfficeId officeId, ServiceId serviceId, Instant startsAt, Instant endsAt) {}
