package co.ufps.clinica.domain.billing;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.Instant;
public record Invoice(InvoiceId id, PatientId patientId, AppointmentId appointmentId, String number, Instant issuedAt,
                      long subtotal, long tax, long total, String currency, InvoiceStatus status) {}
