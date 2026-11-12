package co.ufps.clinica.domain.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class AppointmentTest {
    @Test void patientCancellationStoresReasonAndCannotBeRepeated() {
        Instant startsAt = Instant.now().plus(2, ChronoUnit.DAYS);
        Appointment appointment = Appointment.schedule(PatientId.generate(), DoctorId.generate(), BranchId.generate(),
                OfficeId.generate(), null, startsAt, startsAt.plus(30, ChronoUnit.MINUTES), Instant.now());

        Appointment cancelled = appointment.cancel("Cambio de horario");

        assertEquals(AppointmentStatus.CANCELLED, cancelled.status());
        assertEquals("Cambio de horario", cancelled.cancellationReason());
        assertThrows(IllegalStateException.class, () -> cancelled.cancel("Intento repetido"));
    }
}
