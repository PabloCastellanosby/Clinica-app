package co.ufps.clinica.frameworks.persistence.reports;

import co.ufps.clinica.application.reports.AttendanceMetrics;
import co.ufps.clinica.application.reports.ReportQueries;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.frameworks.persistence.appointment.AppointmentJpa;
import co.ufps.clinica.frameworks.persistence.staff.DoctorJpa;
import co.ufps.clinica.frameworks.persistence.catalog.SpecialtyJpa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class ReportQueriesJpa implements ReportQueries {
    @PersistenceContext EntityManager entityManager;

    @Override public Map<String, Object> dashboard(BranchId branchId, LocalDate from, LocalDate until) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("branchId", branchId == null ? "ALL" : branchId.value().toString());
        report.put("from", from.toString());
        report.put("until", until.toString());
        String filter = branchId == null ? "1 = 1" : "a.branchId = :branchId";
        addMetrics(report, filter, branchId == null ? null : branchId.value(), null, from, until);
        String activeDoctorsQuery = "SELECT COUNT(DISTINCT d) FROM DoctorJpa d WHERE d.status = 'ACTIVE' "
                + "AND EXISTS (SELECT u.id FROM UserJpa u WHERE u.id = d.userId AND u.active = true)"
                + (branchId == null ? "" : " AND EXISTS (SELECT av.id FROM DoctorAvailabilityJpa av WHERE av.doctorId = d.id AND av.branchId = :branchId)");
        var doctorsQuery = entityManager.createQuery(activeDoctorsQuery, Long.class);
        if (branchId != null) doctorsQuery.setParameter("branchId", branchId.value());
        report.put("activeDoctors", doctorsQuery.getSingleResult());
        return report;
    }

    @Override public Map<String, Object> byDoctor(DoctorId doctorId, LocalDate from, LocalDate until) {
        DoctorJpa doctor = entityManager.find(DoctorJpa.class, doctorId.value());
        if (doctor == null) throw new java.util.NoSuchElementException("Médico no encontrado");
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("doctorId", doctorId.value().toString());
        report.put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());
        report.put("from", from.toString()); report.put("until", until.toString());
        addMetrics(report, "a.doctorId = :doctorId", doctorId.value(), null, from, until);
        return report;
    }

    @Override public Map<String, Object> bySpecialty(SpecialtyId specialtyId, LocalDate from, LocalDate until) {
        SpecialtyJpa specialty = entityManager.find(SpecialtyJpa.class, specialtyId.value());
        if (specialty == null) throw new java.util.NoSuchElementException("Especialidad no encontrada");
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("specialtyId", specialtyId.value().toString());
        report.put("specialtyName", specialty.getName());
        report.put("from", from.toString()); report.put("until", until.toString());
        addMetrics(report, "a.doctorId IN (SELECT d.id FROM DoctorJpa d WHERE d.specialtyId = :specialtyId)", null, specialtyId.value(), from, until);
        return report;
    }

    private void addMetrics(Map<String, Object> result, String filter, Object branchOrDoctorId, Object specialtyId,
                            LocalDate from, LocalDate until) {
        Instant start = from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = until.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        String jpql = "SELECT COUNT(a) FROM AppointmentJpa a WHERE " + filter + " AND a.startsAt >= :start AND a.startsAt < :end";
        var query = entityManager.createQuery(jpql, Long.class).setParameter("start", start).setParameter("end", end);
        if (specialtyId != null) query.setParameter("specialtyId", specialtyId);
        else if (filter.contains(":branchId")) query.setParameter("branchId", branchOrDoctorId);
        else if (filter.contains(":doctorId")) query.setParameter("doctorId", branchOrDoctorId);
        long total = query.getSingleResult();
        long cancelled = count(jpql + " AND a.status = 'CANCELLED'", start, end, branchOrDoctorId, specialtyId, filter);
        long noShows = count(jpql + " AND a.status = 'NO_SHOW'", start, end, branchOrDoctorId, specialtyId, filter);
        long completed = count(jpql + " AND a.status = 'COMPLETED'", start, end, branchOrDoctorId, specialtyId, filter);
        AttendanceMetrics metrics = new AttendanceMetrics(total, cancelled, noShows);
        result.put("scheduled", total); result.put("completed", completed);
        result.put("cancelled", cancelled); result.put("noShows", noShows);
        result.put("cancellationRate", metrics.cancellationRate()); result.put("noShowRate", metrics.noShowRate());
    }

    private long count(String jpql, Instant start, Instant end, Object branchOrDoctorId, Object specialtyId, String filter) {
        var query = entityManager.createQuery(jpql, Long.class).setParameter("start", start).setParameter("end", end);
        if (specialtyId != null) query.setParameter("specialtyId", specialtyId);
        else if (filter.contains(":branchId")) query.setParameter("branchId", branchOrDoctorId);
        else if (filter.contains(":doctorId")) query.setParameter("doctorId", branchOrDoctorId);
        return query.getSingleResult();
    }
}
