package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.BranchId;
import co.ufps.clinica.domain.DoctorId;
import co.ufps.clinica.domain.SpecialtyId;
import java.time.*;
import java.util.*;

/** Puertos para agenda médica, notificaciones, reportes, documentos y existencias. */
public final class ClinicOperations {
    private ClinicOperations() {}
    public interface DoctorAgenda { List<Appointment> forDoctor(DoctorId doctorId, Instant from, Instant until); }
    public interface Notifications { void sendAppointmentConfirmation(Appointment appointment); void sendReminder(Appointment appointment); void publish(PatientNotice notice); }
    public interface Documents { byte[] renderPrescriptionPdf(Prescription prescription); byte[] exportReport(String reportType, Map<String, String> filters); }
    public interface Stock { List<StockLot> belowMinimum(BranchId branchId); List<StockLot> expiringBefore(BranchId branchId, LocalDate date); StockLot adjust(StockMovement movement); }
    public interface Reports { Map<String, Object> dashboard(BranchId branchId, LocalDate from, LocalDate until); Map<String, Object> byDoctor(DoctorId doctorId, LocalDate from, LocalDate until); Map<String, Object> bySpecialty(SpecialtyId specialtyId, LocalDate from, LocalDate until); }
    public record AttendanceMetrics(long scheduled, long cancelled, long noShows) {
        public double cancellationRate() { return scheduled == 0 ? 0 : (double) cancelled / scheduled; }
        public double noShowRate() { return scheduled == 0 ? 0 : (double) noShows / scheduled; }
    }
}
