package co.ufps.clinica.application.appointment;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;

public record JoinWaitlistCommand(DoctorId doctorId, SpecialtyId specialtyId, Instant requestedFrom, Instant requestedUntil) {}
