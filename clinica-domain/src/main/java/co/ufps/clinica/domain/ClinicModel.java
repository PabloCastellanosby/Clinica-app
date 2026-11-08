package co.ufps.clinica.domain;

import java.time.*;
import java.util.*;

/** Tipos del modelo clínico. Los identificadores son UUID para facilitar integración distribuida. */
public final class ClinicModel {
    private ClinicModel() {}
    public record User(UserId id, String email, String passwordHash, Role role, boolean active, Instant createdAt, Instant lastLoginAt) {}
    public record PasswordReset(PasswordResetId id, UserId userId, String tokenHash, Instant expiresAt, Instant usedAt) {}
    public record AccessAudit(AccessAuditId id, UserId userId, String action, String ipAddress, Instant occurredAt, boolean successful) {}
    public record Permission(PermissionId id, String key, String description) {}
    public record Clinic(ClinicId id, String name, String taxId, String email, String phone, boolean active) {}
    public record Branch(BranchId id, ClinicId clinicId, String name, String address, String phone, ZoneId timeZone, boolean active) {}
    public record Holiday(HolidayId id, BranchId branchId, LocalDate date, String description) {}
    public record Office(OfficeId id, BranchId branchId, String name, String type, int capacity, boolean active) {}
    public record OpeningHours(OpeningHoursId id, BranchId branchId, DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {}
    public record Specialty(SpecialtyId id, String name, String description, boolean active) {}
    public record Service(ServiceId id, SpecialtyId specialtyId, String name, String description, long priceMinorUnits, String currency, boolean active) {}
    public record Patient(PatientId id, String documentType, String documentNumber, String firstName, String lastName, LocalDate birthDate, String email, String phone, String address, String affiliationType, Instant createdAt) {
        public static Patient register(String documentType, String documentNumber, String firstName, String lastName, LocalDate birthDate, String email, String phone, String address, String affiliationType) {
            return new Patient(PatientId.generate(), documentType, documentNumber, firstName, lastName, birthDate, email, phone, address, affiliationType, Instant.now());
        }
        public String fullName() { return firstName + " " + lastName; }
    }
    public record Doctor(DoctorId id, UserId userId, String licenseNumber, String firstName, String lastName, SpecialtyId specialtyId, StaffStatus status) {}
    public record Staff(StaffId id, UserId userId, String firstName, String lastName, String position, StaffStatus status) {}
    public record DoctorAvailability(DoctorAvailabilityId id, DoctorId doctorId, BranchId branchId, DayOfWeek day, LocalTime startsAt, LocalTime endsAt, LocalDate validFrom, LocalDate validUntil) {}
    public record Appointment(AppointmentId id, PatientId patientId, DoctorId doctorId, BranchId branchId, OfficeId officeId, ServiceId serviceId, Instant startsAt, Instant endsAt, AppointmentStatus status, String cancellationReason, Instant createdAt) {
        public Appointment { if (startsAt == null || endsAt == null || !startsAt.isBefore(endsAt)) throw new IllegalArgumentException("La cita debe tener duración positiva"); }
        public static Appointment schedule(PatientId patientId, DoctorId doctorId, BranchId branchId, OfficeId officeId, ServiceId serviceId, Instant startsAt, Instant endsAt, Instant createdAt) {
            return new Appointment(AppointmentId.generate(), patientId, doctorId, branchId, officeId, serviceId, startsAt, endsAt, AppointmentStatus.REQUESTED, null, createdAt);
        }
        public Appointment withStatus(AppointmentStatus next) {
            if (status == null || next == null || !status.canTransitionTo(next)) throw new IllegalStateException("Transición de cita no permitida: " + status + " -> " + next);
            return new Appointment(id, patientId, doctorId, branchId, officeId, serviceId, startsAt, endsAt, next, cancellationReason, createdAt);
        }
    }
    public record WaitlistEntry(WaitlistEntryId id, PatientId patientId, DoctorId doctorId, SpecialtyId specialtyId, Instant requestedFrom, Instant requestedUntil, Instant createdAt) {}
    public record AppointmentReminder(AppointmentReminderId id, AppointmentId appointmentId, Instant scheduledAt, Instant sentAt, NotificationChannel channel) {}
    public record Encounter(EncounterId id, AppointmentId appointmentId, PatientId patientId, DoctorId doctorId, Instant occurredAt, String reason, String diagnosis, String notes, boolean patientVisible) {}
    public record MedicalHistory(MedicalHistoryId id, PatientId patientId, String type, String description, Instant recordedAt, UserId recordedBy) {}
    public record Allergy(AllergyId id, PatientId patientId, String substance, String reaction, String severity) {}
    public record VitalSigns(VitalSignsId id, EncounterId encounterId, java.math.BigDecimal temperatureC, Integer systolic, Integer diastolic, Integer heartRate, Integer respiratoryRate, java.math.BigDecimal weightKg, java.math.BigDecimal heightCm, Instant measuredAt) {}
    public record Attachment(AttachmentId id, EncounterId encounterId, String fileName, String contentType, String storageKey, long sizeBytes, Instant uploadedAt) {}
    public record Prescription(PrescriptionId id, EncounterId encounterId, PatientId patientId, DoctorId doctorId, Instant issuedAt, String instructions, List<PrescriptionLine> lines) { public Prescription { lines = List.copyOf(lines); } }
    public record PrescriptionLine(PrescriptionLineId id, String medicine, String presentation, String dose, String frequency, int durationDays, int quantity) {}
    public record ExamOrder(ExamOrderId id, EncounterId encounterId, String name, String instructions, ExamOrderStatus status, Instant orderedAt, Instant completedAt, String result) {}
    public record Invoice(InvoiceId id, PatientId patientId, AppointmentId appointmentId, String number, Instant issuedAt, long subtotal, long tax, long total, String currency, InvoiceStatus status) {}
    public record InvoiceLine(InvoiceLineId id, InvoiceId invoiceId, String description, int quantity, long unitPrice, long total) {}
    public record Medicine(MedicineId id, String name, String presentation, String dosageForm, String unit, boolean active) {}
    public record StockLot(StockLotId id, BranchId branchId, MedicineId medicineId, String lotNumber, int quantity, LocalDate expiresAt, long unitCost) {}
    public record StockMovement(StockMovementId id, StockLotId lotId, StockMovementType type, int quantity, String reason, UserId actorId, Instant occurredAt) {}
    public record TransactionalMessage(TransactionalMessageId id, UserId recipientId, String destination, String subject, String template, MessageStatus status, Instant createdAt, Instant sentAt) {}
    public record PatientNotice(PatientNoticeId id, String title, String body, Instant publishedAt, Instant expiresAt, BranchId branchId) {}
}
