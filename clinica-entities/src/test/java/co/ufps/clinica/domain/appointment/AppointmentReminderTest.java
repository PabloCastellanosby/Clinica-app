package co.ufps.clinica.domain.appointment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.domain.communication.NotificationChannel;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AppointmentReminderTest {
    @Test void claimingIncrementsAttemptAndDeliveryResultDoesNotIncrementItAgain() {
        Instant claimedAt = Instant.now();
        AppointmentReminder reminder = new AppointmentReminder(AppointmentReminderId.generate(),
                AppointmentId.generate(), claimedAt.minusSeconds(30), null,
                NotificationChannel.EMAIL, 0, null);

        AppointmentReminder claimed = reminder.claimedAt(claimedAt);
        AppointmentReminder delivered = claimed.deliveryResult(true, claimedAt.plusSeconds(1));

        assertEquals(1, delivered.attempts());
        assertEquals(claimedAt, delivered.lastAttemptAt());
        assertEquals(claimedAt.plusSeconds(1), delivered.sentAt());
    }

    @Test void cannotClaimAnExhaustedReminder() {
        Instant now = Instant.now();
        AppointmentReminder exhausted = new AppointmentReminder(AppointmentReminderId.generate(),
                AppointmentId.generate(), now.minusSeconds(60), null,
                NotificationChannel.EMAIL, 5, now.minusSeconds(600));

        assertThrows(IllegalStateException.class, () -> exhausted.claimedAt(now));
    }
}
