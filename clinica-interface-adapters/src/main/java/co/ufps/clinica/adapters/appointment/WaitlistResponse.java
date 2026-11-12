package co.ufps.clinica.adapters.appointment;

import java.time.Instant;

public record WaitlistResponse(String id, String doctorId, String specialtyId, Instant requestedFrom,
                               Instant requestedUntil, Instant createdAt, String status) {}
