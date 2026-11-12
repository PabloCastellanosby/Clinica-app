package co.ufps.clinica.application.billing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.billing.Invoice;
import co.ufps.clinica.domain.billing.InvoiceId;
import co.ufps.clinica.domain.billing.InvoiceStatus;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InvoiceServiceTest {
    @Test void rejectsAnAppointmentThatAlreadyHasAnInvoice() {
        Patient patient = patient();
        Appointment appointment = appointment(patient.id());
        Invoice existing = new Invoice(InvoiceId.generate(), patient.id(), appointment.id(), "INV-1", Instant.now(),
                1000, 0, 1000, "COP", InvoiceStatus.PENDING);
        InvoiceService service = service(patient, appointment, existing);
        var command = new CreateInvoiceCommand(patient.id(), appointment.id(),
                List.of(new CreateInvoiceCommand.InvoiceLineDto("Consulta", 1, 1000)), 0, "COP");

        assertThrows(IllegalStateException.class, () -> service.createInvoice(command));
    }

    @Test void calculatesInvoiceTotalsInMinorCurrencyUnits() {
        Patient patient = patient();
        Appointment appointment = appointment(patient.id());
        InvoiceService service = service(patient, appointment, null);
        var command = new CreateInvoiceCommand(patient.id(), appointment.id(),
                List.of(new CreateInvoiceCommand.InvoiceLineDto("Consulta", 2, 10000)), 19, "COP");

        Invoice invoice = service.createInvoice(command);

        assertEquals(20000, invoice.subtotal());
        assertEquals(3800, invoice.tax());
        assertEquals(23800, invoice.total());
    }

    private InvoiceService service(Patient patient, Appointment appointment, Invoice existing) {
        PatientRepository patients = proxy(PatientRepository.class, (method, args) ->
                method.equals("findById") ? Optional.of(patient) : defaultValue(method));
        AppointmentRepository appointments = proxy(AppointmentRepository.class, (method, args) ->
                method.equals("findById") ? Optional.of(appointment) : defaultValue(method));
        InvoiceRepository invoices = proxy(InvoiceRepository.class, (method, args) -> switch (method) {
            case "findByAppointment" -> Optional.ofNullable(existing);
            case "saveWithLines" -> (Invoice) args[0];
            case "findById", "findByNumber" -> Optional.empty();
            case "findByPatient", "findByInvoice" -> List.of();
            default -> defaultValue(method);
        });
        return new InvoiceService(invoices, patients, appointments);
    }

    private interface Invocation { Object call(String method, Object[] args); }
    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (proxy, method, args) -> invocation.call(method.getName(), args));
    }
    private static Object defaultValue(String method) { return null; }
    private static Patient patient() {
        return Patient.register("CC", "123", "Ana", "López", null, null, null, null, null);
    }
    private static Appointment appointment(PatientId patientId) {
        Instant start = Instant.now().plusSeconds(3600);
        return Appointment.schedule(patientId, DoctorId.generate(), BranchId.generate(), OfficeId.generate(), null,
                start, start.plusSeconds(1800), Instant.now());
    }
}
