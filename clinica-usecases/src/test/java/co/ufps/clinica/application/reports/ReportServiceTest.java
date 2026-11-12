package co.ufps.clinica.application.reports;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReportServiceTest {
    private static final Map<String, Object> REPORT = Map.of("doctorName", "López, Ana", "cancellationRate", 0.25);

    @Test void exportsCsvWithEscapedValues() {
        ReportService service = new ReportService(queries());
        String csv = new String(service.exportCsv(REPORT), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals("campo,valor\r\n\"cancellationRate\",\"0.25\"\r\n\"doctorName\",\"López, Ana\"\r\n", csv);
    }

    @Test void neutralizesSpreadsheetFormulaValues() {
        ReportService service = new ReportService(queries());
        String csv = new String(service.exportCsv(Map.of("doctorName", "=HYPERLINK(\"https://bad\")")),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals("campo,valor\r\n\"doctorName\",\"'=HYPERLINK(\"\"https://bad\"\")\"\r\n", csv);
    }

    @Test void rejectsAnInvertedDateRange() {
        ReportService service = new ReportService(queries());
        assertThrows(IllegalArgumentException.class, () -> service.dashboard(
                BranchId.of(UUID.randomUUID()), LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 31)));
    }

    @Test void allowsTheDashboardToAggregateAllBranches() {
        ReportService service = new ReportService(queries());
        assertEquals(REPORT, service.dashboard(null, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
    }

    private ReportQueries queries() {
        return new ReportQueries() {
            @Override public Map<String, Object> dashboard(BranchId branchId, LocalDate from, LocalDate until) { return REPORT; }
            @Override public Map<String, Object> byDoctor(DoctorId doctorId, LocalDate from, LocalDate until) { return REPORT; }
            @Override public Map<String, Object> bySpecialty(SpecialtyId specialtyId, LocalDate from, LocalDate until) { return REPORT; }
        };
    }
}
