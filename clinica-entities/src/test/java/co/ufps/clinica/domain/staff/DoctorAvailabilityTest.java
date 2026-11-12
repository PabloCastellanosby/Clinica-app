package co.ufps.clinica.domain.staff;

import co.ufps.clinica.domain.clinic.BranchId;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoctorAvailabilityTest {
    @Test
    void acceptsValidAvailability() {
        assertDoesNotThrow(() -> new DoctorAvailability(DoctorAvailabilityId.generate(), DoctorId.generate(),
                BranchId.generate(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                LocalDate.of(2026, 1, 1), null));
    }

    @Test
    void rejectsInvalidTimeAndDateRanges() {
        assertThrows(IllegalArgumentException.class, () -> new DoctorAvailability(DoctorAvailabilityId.generate(),
                DoctorId.generate(), BranchId.generate(), DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.NOON,
                LocalDate.of(2026, 1, 1), null));
        assertThrows(IllegalArgumentException.class, () -> new DoctorAvailability(DoctorAvailabilityId.generate(),
                DoctorId.generate(), BranchId.generate(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.NOON,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
    }
}
