package co.ufps.clinica.application.appointment;

import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.catalog.ServiceId;
import java.time.Instant;

public record ScheduleAppointmentCommand(PatientId patientId, DoctorId doctorId, BranchId branchId,
                                         OfficeId officeId, ServiceId serviceId, Instant startsAt, Instant endsAt) {}
