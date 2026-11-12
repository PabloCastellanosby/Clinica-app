package co.ufps.clinica.adapters.appointment;

import java.time.Instant;

public record AppointmentResponse(String id, String patientId, String doctorId, String branchId,
                                  String officeId, String serviceId, Instant startsAt, Instant endsAt,
                                  String status, String cancellationReason) {}
