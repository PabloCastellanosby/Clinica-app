package co.ufps.clinica.frameworks.persistence.appointment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "APPOINTMENT_REMINDERS", indexes = @Index(name = "idx_reminder_due", columnList = "sent_at, scheduled_at"),
       uniqueConstraints = @UniqueConstraint(name = "uq_reminder_appointment", columnNames = "appointment_id"))
public class AppointmentReminderJpa {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "appointment_id", nullable = false) private UUID appointmentId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "appointment_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_appointment_reminders_appointment"))
    private co.ufps.clinica.frameworks.persistence.appointment.AppointmentJpa appointmentReference;
    @Column(name = "scheduled_at", nullable = false) private Instant scheduledAt;
    @Column(name = "sent_at") private Instant sentAt;
    @Column(name = "channel", length = 20, nullable = false) private String channel;
    @Column(name = "attempts", nullable = false) private int attempts;
    @Column(name = "last_attempt_at") private Instant lastAttemptAt;
    public AppointmentReminderJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public Instant getLastAttemptAt() { return lastAttemptAt; }
    public void setLastAttemptAt(Instant lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; }
}
