package co.ufps.clinica.infrastructure.persistence;

import co.ufps.clinica.domain.AppointmentRepository;
import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.AppointmentStatus;
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
    @Override public boolean hasConflict(UUID doctorId,UUID officeId,Instant from,Instant until) {
        String sql="SELECT COUNT(*) FROM appointments WHERE status NOT IN ('CANCELLED','NO_SHOW') AND starts_at < ? AND ends_at > ? AND (doctor_id=? OR office_id=?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setTimestamp(1,Timestamp.from(until));s.setTimestamp(2,Timestamp.from(from));s.setObject(3,doctorId);s.setObject(4,officeId);
            try(ResultSet r=s.executeQuery()){r.next();return r.getInt(1)>0;}
        }catch(SQLException e){throw new InfrastructureException("No se pudo comprobar disponibilidad",e);}
    }
    @Override public Optional<Appointment> findById(UUID id) {
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM appointments WHERE id=?")) {
            s.setObject(1,id);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(read(r)):Optional.empty();}
        }catch(SQLException e){throw new InfrastructureException("No se pudo consultar la cita",e);}
    }
    @Override public Appointment save(Appointment a) {
        String sql="MERGE INTO appointments (id,patient_id,doctor_id,branch_id,office_id,service_id,starts_at,ends_at,status,cancellation_reason,created_at) KEY(id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setObject(1,a.id());s.setObject(2,a.patientId());s.setObject(3,a.doctorId());s.setObject(4,a.branchId());s.setObject(5,a.officeId());s.setObject(6,a.serviceId());
            s.setTimestamp(7,Timestamp.from(a.startsAt()));s.setTimestamp(8,Timestamp.from(a.endsAt()));s.setString(9,a.status().name());s.setString(10,a.cancellationReason());s.setTimestamp(11,Timestamp.from(a.createdAt()==null?Instant.now():a.createdAt()));s.executeUpdate();return a;
        }catch(SQLException e){throw new InfrastructureException("No se pudo guardar la cita",e);}
    }
    private static Appointment read(ResultSet r)throws SQLException {
        return new Appointment(r.getObject("id",UUID.class),r.getObject("patient_id",UUID.class),r.getObject("doctor_id",UUID.class),r.getObject("branch_id",UUID.class),r.getObject("office_id",UUID.class),r.getObject("service_id",UUID.class),r.getTimestamp("starts_at").toInstant(),r.getTimestamp("ends_at").toInstant(),AppointmentStatus.valueOf(r.getString("status")),r.getString("cancellation_reason"),r.getTimestamp("created_at").toInstant());
    }
}
