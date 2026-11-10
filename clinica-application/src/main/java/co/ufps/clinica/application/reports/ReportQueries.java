package co.ufps.clinica.application.reports;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.LocalDate;
import java.util.Map;
public interface ReportQueries {
    Map<String, Object> dashboard(BranchId branchId, LocalDate from, LocalDate until);
    Map<String, Object> byDoctor(DoctorId doctorId, LocalDate from, LocalDate until);
    Map<String, Object> bySpecialty(SpecialtyId specialtyId, LocalDate from, LocalDate until);
}
