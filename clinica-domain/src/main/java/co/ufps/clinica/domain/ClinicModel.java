package co.ufps.clinica.domain;

import java.time.*;
import java.util.*;

/** Tipos del modelo clínico. Los identificadores son UUID para facilitar integración distribuida. */
public final class ClinicModel {
    private ClinicModel() {}
    public record User(UUID id, String email, String passwordHash, Role role, boolean active, Instant createdAt, Instant lastLoginAt) {}
    public record PasswordReset(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant usedAt) {}
    public record AccessAudit(UUID id, UUID userId, String action, String ipAddress, Instant occurredAt, boolean successful) {}
    public record Permission(UUID id, String key, String description) {}
    public record Clinic(UUID id, String name, String taxId, String email, String phone, boolean active) {}
    public record Branch(UUID id, UUID clinicId, String name, String address, String phone, ZoneId timeZone, boolean active) {}
    public record Holiday(UUID id, UUID branchId, LocalDate date, String description) {}
    public record Office(UUID id, UUID branchId, String name, String type, int capacity, boolean active) {}
    public record OpeningHours(UUID id, UUID branchId, DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {}
    public record Specialty(UUID id, String name, String description, boolean active) {}
    public record Service(UUID id, UUID specialtyId, String name, String description, long priceMinorUnits, String currency, boolean active) {}
    public record Patient(UUID id, String documentType, String documentNumber, String firstName, String lastName, LocalDate birthDate, String email, String phone, String address, String affiliationType, Instant createdAt) {
        public String fullName() { return firstName + " " + lastName; }
    }
    public record Doctor(UUID id, UUID userId, String licenseNumber, String firstName, String lastName, UUID specialtyId, StaffStatus status) {}
    public record Staff(UUID id, UUID userId, String firstName, String lastName, String position, StaffStatus status) {}
    public record DoctorAvailability(UUID id, UUID doctorId, UUID branchId, DayOfWeek day, LocalTime startsAt, LocalTime endsAt, LocalDate validFrom, LocalDate validUntil) {}
    public record Appointment(UUID id, UUID patientId, UUID doctorId, UUID branchId, UUID officeId, UUID serviceId, Instant startsAt, Instant endsAt, AppointmentStatus status, String cancellationReason, Instant createdAt) {
        public Appointment { if (startsAt == null || endsAt == null || !startsAt.isBefore(endsAt)) throw new IllegalArgumentException("La cita debe tener duración positiva"); }
        public Appointment withStatus(AppointmentStatus next) {
            if (status == null || next == null || !status.canTransitionTo(next)) throw new IllegalStateException("Transición de cita no permitida: " + status + " -> " + next);
            return new Appointment(id, patientId, doctorId, branchId, officeId, serviceId, startsAt, endsAt, next, cancellationReason, createdAt);
        }
    }
    public record WaitlistEntry(UUID id, UUID patientId, UUID doctorId, UUID specialtyId, Instant requestedFrom, Instant requestedUntil, Instant createdAt) {}
    public record AppointmentReminder(UUID id, UUID appointmentId, Instant scheduledAt, Instant sentAt, NotificationChannel channel) {}
    public record Encounter(UUID id, UUID appointmentId, UUID patientId, UUID doctorId, Instant occurredAt, String reason, String diagnosis, String notes, boolean patientVisible) {}
    public record MedicalHistory(UUID id, UUID patientId, String type, String description, Instant recordedAt, UUID recordedBy) {}
    public record Allergy(UUID id, UUID patientId, String substance, String reaction, String severity) {}
    public record VitalSigns(UUID id, UUID encounterId, java.math.BigDecimal temperatureC, Integer systolic, Integer diastolic, Integer heartRate, Integer respiratoryRate, java.math.BigDecimal weightKg, java.math.BigDecimal heightCm, Instant measuredAt) {}
    public record Attachment(UUID id, UUID encounterId, String fileName, String contentType, String storageKey, long sizeBytes, Instant uploadedAt) {}
    public record Prescription(UUID id, UUID encounterId, UUID patientId, UUID doctorId, Instant issuedAt, String instructions, List<PrescriptionLine> lines) { public Prescription { lines = List.copyOf(lines); } }
    public record PrescriptionLine(UUID id, String medicine, String presentation, String dose, String frequency, int durationDays, int quantity) {}
    public record ExamOrder(UUID id, UUID encounterId, String name, String instructions, ExamOrderStatus status, Instant orderedAt, Instant completedAt, String result) {}
    public record Invoice(UUID id, UUID patientId, UUID appointmentId, String number, Instant issuedAt, long subtotal, long tax, long total, String currency, InvoiceStatus status) {}
    public record InvoiceLine(UUID id, UUID invoiceId, String description, int quantity, long unitPrice, long total) {}
    public record Medicine(UUID id, String name, String presentation, String dosageForm, String unit, boolean active) {}
    public record StockLot(UUID id, UUID branchId, UUID medicineId, String lotNumber, int quantity, LocalDate expiresAt, long unitCost) {}
    public record StockMovement(UUID id, UUID lotId, StockMovementType type, int quantity, String reason, UUID actorId, Instant occurredAt) {}
    public record TransactionalMessage(UUID id, UUID recipientId, String destination, String subject, String template, MessageStatus status, Instant createdAt, Instant sentAt) {}
    public record PatientNotice(UUID id, String title, String body, Instant publishedAt, Instant expiresAt, UUID branchId) {}
}
