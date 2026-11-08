package co.ufps.clinica.infrastructure.persistence;

import co.ufps.clinica.domain.ClinicModel.Patient;
import co.ufps.clinica.domain.PatientRepository;
import co.ufps.clinica.domain.PatientId;
import co.ufps.clinica.infrastructure.exception.InfrastructureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.time.*;
import java.util.*;

@ApplicationScoped
public class PatientJdbcRepository implements PatientRepository {
    private final DataSource dataSource;
    @Inject public PatientJdbcRepository(DataSource dataSource) { this.dataSource = dataSource; }

    @Override public Optional<Patient> findByDocument(String type, String number) {
        return one("SELECT * FROM patients WHERE document_type=? AND document_number=?", type, number);
    }
    @Override public Optional<Patient> findById(PatientId id) { return one("SELECT * FROM patients WHERE id=?", id.value()); }
    private Optional<Patient> one(String sql, Object... values) {
        try (Connection c=dataSource.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            bind(s, values); try (ResultSet r=s.executeQuery()) { return r.next() ? Optional.of(read(r)) : Optional.empty(); }
        } catch (SQLException e) { throw new InfrastructureException("No se pudo consultar el paciente", e); }
    }
    @Override public Patient save(Patient p) {
        String update="UPDATE patients SET document_type=?,document_number=?,first_name=?,last_name=?,birth_date=?,email=?,phone=?,address=?,affiliation_type=? WHERE id=?";
        try (Connection c=dataSource.getConnection(); PreparedStatement s=c.prepareStatement(update)) {
            patientFields(s,p); s.setObject(10,p.id().value());
            if (s.executeUpdate()==0) {
                String insert="INSERT INTO patients(id,document_type,document_number,first_name,last_name,birth_date,email,phone,address,affiliation_type,created_at) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement i=c.prepareStatement(insert)) { i.setObject(1,p.id().value()); patientFields(i,p,2); i.setTimestamp(11,Timestamp.from(p.createdAt()==null?Instant.now():p.createdAt())); i.executeUpdate(); }
            }
            return p;
        } catch (SQLException e) { throw new InfrastructureException("No se pudo guardar el paciente; verifique que su documento no esté duplicado",e); }
    }
    @Override public List<Patient> search(String query) {
        String sql="SELECT * FROM patients WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? OR document_number LIKE ? ORDER BY last_name,first_name";
        String term="%"+(query==null?"":query.toLowerCase(Locale.ROOT))+"%";
        try (Connection c=dataSource.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setString(1,term);s.setString(2,term);s.setString(3,term);
            try(ResultSet r=s.executeQuery()){List<Patient> results=new ArrayList<>();while(r.next())results.add(read(r));return results;}
        } catch(SQLException e){throw new InfrastructureException("No se pudo buscar pacientes",e);}
    }
    private static Patient read(ResultSet r)throws SQLException {
        java.sql.Date birth=r.getDate("birth_date"); Timestamp created=r.getTimestamp("created_at");
        return new Patient(PatientId.of(r.getObject("id",UUID.class)),r.getString("document_type"),r.getString("document_number"),r.getString("first_name"),r.getString("last_name"),birth==null?null:birth.toLocalDate(),r.getString("email"),r.getString("phone"),r.getString("address"),r.getString("affiliation_type"),created==null?null:created.toInstant());
    }
    private static void patientFields(PreparedStatement s,Patient p)throws SQLException{patientFields(s,p,1);}
    private static void patientFields(PreparedStatement s,Patient p,int start)throws SQLException {
        s.setString(start,p.documentType());s.setString(start+1,p.documentNumber());s.setString(start+2,p.firstName());s.setString(start+3,p.lastName());
        if(p.birthDate()==null)s.setNull(start+4,Types.DATE);else s.setDate(start+4,java.sql.Date.valueOf(p.birthDate()));
        s.setString(start+5,p.email());s.setString(start+6,p.phone());s.setString(start+7,p.address());s.setString(start+8,p.affiliationType());
    }
    private static void bind(PreparedStatement s,Object... values)throws SQLException{for(int i=0;i<values.length;i++)s.setObject(i+1,values[i]);}
}
