package co.ufps.clinica.application.reports;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public final class ReportService {
    private final ReportQueries queries;
    public ReportService(ReportQueries queries) { this.queries = Objects.requireNonNull(queries); }

    public Map<String, Object> dashboard(BranchId branchId, LocalDate from, LocalDate until) {
        validateRange(from, until);
        return queries.dashboard(branchId, from, until);
    }
    public Map<String, Object> byDoctor(DoctorId doctorId, LocalDate from, LocalDate until) {
        validateRange(from, until);
        return queries.byDoctor(Objects.requireNonNull(doctorId), from, until);
    }
    public Map<String, Object> bySpecialty(SpecialtyId specialtyId, LocalDate from, LocalDate until) {
        validateRange(from, until);
        return queries.bySpecialty(Objects.requireNonNull(specialtyId), from, until);
    }
    public byte[] exportCsv(Map<String, Object> report) {
        StringBuilder csv = new StringBuilder("campo,valor\r\n");
        new TreeMap<>(report).forEach((key, value) -> csv.append(quote(key)).append(',').append(quote(String.valueOf(value))).append("\r\n"));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
    private void validateRange(LocalDate from, LocalDate until) {
        if (from == null || until == null || from.isAfter(until) || until.isAfter(LocalDate.now().plusYears(2)))
            throw new IllegalArgumentException("El intervalo del reporte debe ser válido");
    }
    private String quote(String text) {
        int firstVisible = 0;
        while (firstVisible < text.length() && Character.isWhitespace(text.charAt(firstVisible))) firstVisible++;
        if (firstVisible < text.length() && "=+-@".indexOf(text.charAt(firstVisible)) >= 0) text = "'" + text;
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
