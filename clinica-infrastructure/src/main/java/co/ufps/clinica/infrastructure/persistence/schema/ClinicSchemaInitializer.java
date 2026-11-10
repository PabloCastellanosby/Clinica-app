package co.ufps.clinica.infrastructure.persistence.schema;

import co.ufps.clinica.infrastructure.exception.InfrastructureException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/** Inicializa esquema local para el perfil inicial; sustituir por migraciones Flyway/Liquibase en despliegues. */
@ApplicationScoped
public class ClinicSchemaInitializer {
    private final DataSource dataSource;
    @Inject public ClinicSchemaInitializer(DataSource dataSource) { this.dataSource=dataSource; }
    @PostConstruct void initialize() {
        List<String> ddl=List.of(
            "CREATE TABLE IF NOT EXISTS patients (id UUID PRIMARY KEY, document_type VARCHAR(40) NOT NULL, document_number VARCHAR(80) NOT NULL, first_name VARCHAR(120) NOT NULL, last_name VARCHAR(120) NOT NULL, birth_date DATE, email VARCHAR(254), phone VARCHAR(40), address VARCHAR(500), affiliation_type VARCHAR(80), created_at TIMESTAMP WITH TIME ZONE NOT NULL, CONSTRAINT uq_patient_document UNIQUE(document_type,document_number))",
            "CREATE TABLE IF NOT EXISTS appointments (id UUID PRIMARY KEY, patient_id UUID NOT NULL, doctor_id UUID NOT NULL, branch_id UUID NOT NULL, office_id UUID NOT NULL, service_id UUID, starts_at TIMESTAMP WITH TIME ZONE NOT NULL, ends_at TIMESTAMP WITH TIME ZONE NOT NULL, status VARCHAR(30) NOT NULL, cancellation_reason VARCHAR(500), created_at TIMESTAMP WITH TIME ZONE NOT NULL)",
            "CREATE INDEX IF NOT EXISTS ix_appointment_doctor_time ON appointments(doctor_id,starts_at,ends_at)",
            "CREATE INDEX IF NOT EXISTS ix_appointment_office_time ON appointments(office_id,starts_at,ends_at)",
            "CREATE TABLE IF NOT EXISTS users (id UUID PRIMARY KEY, email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(500) NOT NULL, role VARCHAR(40) NOT NULL, active BOOLEAN NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL, last_login_at TIMESTAMP WITH TIME ZONE)",
            "CREATE TABLE IF NOT EXISTS password_resets (id UUID PRIMARY KEY, user_id UUID NOT NULL, token_hash VARCHAR(500) NOT NULL UNIQUE, expires_at TIMESTAMP WITH TIME ZONE NOT NULL, used_at TIMESTAMP WITH TIME ZONE)",
            "CREATE TABLE IF NOT EXISTS access_audit (id UUID PRIMARY KEY, user_id UUID, action VARCHAR(80) NOT NULL, ip_address VARCHAR(80), occurred_at TIMESTAMP WITH TIME ZONE NOT NULL, successful BOOLEAN NOT NULL)"
        );
        try(Connection connection=dataSource.getConnection();Statement statement=connection.createStatement()) {
            for(String sql:ddl) statement.execute(sql);
        } catch(SQLException e) { throw new InfrastructureException("No se pudo inicializar el esquema de Clínica",e); }
    }
}
