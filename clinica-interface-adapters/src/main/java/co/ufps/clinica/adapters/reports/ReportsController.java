package co.ufps.clinica.adapters.reports;

import co.ufps.clinica.application.reports.ReportService;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ReportsController {
    private final ReportService service;
    public ReportsController(ReportService service) { this.service = Objects.requireNonNull(service); }
    public Map<String, Object> dashboard(String branchId, LocalDate from, LocalDate until) {
        BranchId scope = branchId == null || branchId.isBlank() ? null : BranchId.of(UUID.fromString(branchId));
        return service.dashboard(scope, from, until);
    }
    public Map<String, Object> byDoctor(String doctorId, LocalDate from, LocalDate until) {
        return service.byDoctor(DoctorId.of(UUID.fromString(doctorId)), from, until);
    }
    public Map<String, Object> bySpecialty(String specialtyId, LocalDate from, LocalDate until) {
        return service.bySpecialty(SpecialtyId.of(UUID.fromString(specialtyId)), from, until);
    }
    public byte[] exportCsv(Map<String, Object> report) { return service.exportCsv(report); }
}
