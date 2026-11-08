package co.ufps.clinica.infrastructure.persistence;

import co.ufps.clinica.domain.AppointmentRepository;
import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.AppointmentStatus;
import co.ufps.clinica.domain.AppointmentId;
import co.ufps.clinica.domain.PatientId;
import co.ufps.clinica.domain.DoctorId;
import co.ufps.clinica.domain.BranchId;
import co.ufps.clinica.domain.OfficeId;
import co.ufps.clinica.domain.ServiceId;
import co.ufps.clinica.infrastructure.exception.InfrastructureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class AppointmentJdbcRepository implements AppointmentRepository {
    private final DataSource dataSource;
    @Inject public AppointmentJdbcRepository(DataSource dataSource) { this.dataSource=dataSource; }
    @Override public boolean hasConflict(DoctorId doctorId,OfficeId officeId,Instant from,Instant until) {
        String sql="SELECT COUNT(*) FROM appointments WHERE status NOT IN ('CANCELLED','NO_SHOW') AND starts_at < ? AND ends_at > ? AND (doctor_id=? OR office_id=?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setTimestamp(1,Timestamp.from(until));s.setTimestamp(2,Timestamp.from(from));s.setObject(3,doctorId.value());s.setObject(4,officeId.value());
            try(ResultSet r=s.executeQuery()){r.next();return r.getInt(1)>0;}
        }catch(SQLException e){throw new InfrastructureException("No se pudo comprobar disponibilidad",e);}
    }
    @Override public Optional<Appointment> findById(AppointmentId id) {
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM appointments WHERE id=?")) {
            s.setObject(1,id.value());try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(read(r)):Optional.empty();}
        }catch(SQLException e){throw new InfrastructureException("No se pudo consultar la cita",e);}
    }
    @Override public Appointment save(Appointment a) {
        String sql="MERGE INTO appointments (id,patient_id,doctor_id,branch_id,office_id,service_id,starts_at,ends_at,status,cancellation_reason,created_at) KEY(id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setObject(1,a.id().value());s.setObject(2,a.patientId().value());s.setObject(3,a.doctorId().value());s.setObject(4,a.branchId().value());s.setObject(5,a.officeId().value());s.setObject(6,a.serviceId()==null?null:a.serviceId().value());
            s.setTimestamp(7,Timestamp.from(a.startsAt()));s.setTimestamp(8,Timestamp.from(a.endsAt()));s.setString(9,a.status().name());s.setString(10,a.cancellationReason());s.setTimestamp(11,Timestamp.from(a.createdAt()==null?Instant.now():a.createdAt()));s.executeUpdate();return a;
        }catch(SQLException e){throw new InfrastructureException("No se pudo guardar la cita",e);}
    }
    private static Appointment read(ResultSet r)throws SQLException {
        UUID serviceId=r.getObject("service_id",UUID.class);
        return new Appointment(AppointmentId.of(r.getObject("id",UUID.class)),PatientId.of(r.getObject("patient_id",UUID.class)),DoctorId.of(r.getObject("doctor_id",UUID.class)),BranchId.of(r.getObject("branch_id",UUID.class)),OfficeId.of(r.getObject("office_id",UUID.class)),serviceId==null?null:ServiceId.of(serviceId),r.getTimestamp("starts_at").toInstant(),r.getTimestamp("ends_at").toInstant(),AppointmentStatus.valueOf(r.getString("status")),r.getString("cancellation_reason"),r.getTimestamp("created_at").toInstant());
    }
}
