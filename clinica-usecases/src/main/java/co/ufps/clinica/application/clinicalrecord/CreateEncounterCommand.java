package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;

public record CreateEncounterCommand(AppointmentId appointmentId, PatientId patientId, DoctorId doctorId,
                                     String reason, String diagnosis, String notes, boolean patientVisible) {}
