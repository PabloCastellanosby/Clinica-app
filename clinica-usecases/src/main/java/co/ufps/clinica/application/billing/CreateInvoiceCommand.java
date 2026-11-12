package co.ufps.clinica.application.billing;

import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;

public record CreateInvoiceCommand(PatientId patientId, AppointmentId appointmentId,
                                   java.util.List<InvoiceLineDto> lines, int taxRate, String currency) {
    public CreateInvoiceCommand {
        if (patientId == null || lines == null || lines.isEmpty()) throw new IllegalArgumentException("Paciente y al menos una línea son obligatorios");
        if (taxRate < 0 || taxRate > 100) throw new IllegalArgumentException("La tasa de impuesto debe estar entre 0 y 100");
        if (currency == null || !currency.matches("[A-Z]{3}")) throw new IllegalArgumentException("La moneda debe ser un código ISO de tres letras en mayúsculas");
        lines = java.util.List.copyOf(lines);
    }
    public record InvoiceLineDto(String description, int quantity, long unitPrice) {}
}
