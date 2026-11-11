package co.ufps.clinica.application.billing;

import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;

public record CreateInvoiceCommand(PatientId patientId, AppointmentId appointmentId,
                                   java.util.List<InvoiceLineDto> lines, int taxRate, String currency) {
    public record InvoiceLineDto(String description, int quantity, long unitPrice) {}
}
