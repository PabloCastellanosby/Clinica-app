package co.ufps.clinica.api.reports;

import co.ufps.clinica.adapters.reports.ReportsController;
import co.ufps.clinica.api.security.RequireRoles;
import co.ufps.clinica.domain.identity.Role;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Map;

@Path("/api/reports")
@RequireRoles({Role.ADMINISTRADOR})
public class ReportsResource {
    @Inject ReportsController reports;
    @GET @Path("/dashboard") public Map<String, Object> dashboard(@QueryParam("branchId") String branchId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        return reports.dashboard(branchId, from, until);
    }
    @GET @Path("/doctor/{doctorId}") public Map<String, Object> byDoctor(@PathParam("doctorId") String doctorId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        return reports.byDoctor(doctorId, from, until);
    }
    @GET @Path("/specialty/{specialtyId}") public Map<String, Object> bySpecialty(@PathParam("specialtyId") String specialtyId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        return reports.bySpecialty(specialtyId, from, until);
    }
    @GET @Path("/dashboard.csv") public Response dashboardCsv(@QueryParam("branchId") String branchId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        Map<String, Object> report = reports.dashboard(branchId, from, until);
        return csv(report, "clinic-dashboard.csv");
    }
    @GET @Path("/doctor/{doctorId}.csv") public Response doctorCsv(@PathParam("doctorId") String doctorId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        return csv(reports.byDoctor(doctorId, from, until), "clinic-doctor-report.csv");
    }
    @GET @Path("/specialty/{specialtyId}.csv") public Response specialtyCsv(@PathParam("specialtyId") String specialtyId,
            @QueryParam("from") LocalDate from, @QueryParam("until") LocalDate until) {
        return csv(reports.bySpecialty(specialtyId, from, until), "clinic-specialty-report.csv");
    }
    private Response csv(Map<String, Object> report, String filename) {
        return Response.ok(reports.exportCsv(report), "text/csv; charset=utf-8")
                .header("Content-Disposition", "attachment; filename=" + filename).build();
    }
}
