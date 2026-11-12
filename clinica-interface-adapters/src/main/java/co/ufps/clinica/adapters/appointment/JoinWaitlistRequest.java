package co.ufps.clinica.adapters.appointment;

import java.time.Instant;

public record JoinWaitlistRequest(String doctorId, String specialtyId, Instant requestedFrom, Instant requestedUntil) {}
