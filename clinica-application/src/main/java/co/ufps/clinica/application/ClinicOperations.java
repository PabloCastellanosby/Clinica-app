package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.*;
import java.time.*;
import java.util.*;

/** Puertos para agenda médica, notificaciones, reportes, documentos y existencias. */
public final class ClinicOperations {
    private ClinicOperations() {}
    public interface DoctorAgenda { List<Appointment> forDoctor(UUID doctorId, Instant from, Instant until); }
    public interface Notifications { void sendAppointmentConfirmation(Appointment appointment); void sendReminder(Appointment appointment); void publish(PatientNotice notice); }
    public interface Documents { byte[] renderPrescriptionPdf(Prescription prescription); byte[] exportReport(String reportType, Map<String, String> filters); }
    public interface Stock { List<StockLot> belowMinimum(UUID branchId); List<StockLot> expiringBefore(UUID branchId, LocalDate date); StockLot adjust(StockMovement movement); }
    public interface Reports { Map<String, Object> dashboard(UUID branchId, LocalDate from, LocalDate until); Map<String, Object> byDoctor(UUID doctorId, LocalDate from, LocalDate until); Map<String, Object> bySpecialty(UUID specialtyId, LocalDate from, LocalDate until); }
    public record AttendanceMetrics(long scheduled, long cancelled, long noShows) {
        public double cancellationRate() { return scheduled == 0 ? 0 : (double) cancelled / scheduled; }
        public double noShowRate() { return scheduled == 0 ? 0 : (double) noShows / scheduled; }
    }
}
