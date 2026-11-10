package co.ufps.clinica.infrastructure.persistence.identity;

import co.ufps.clinica.domain.identity.*;
import co.ufps.clinica.infrastructure.exception.InfrastructureException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class UserJdbcRepository implements UserRepository {
    private final DataSource dataSource;
    @Inject public UserJdbcRepository(DataSource dataSource) { this.dataSource=dataSource; }
    @Override public Optional<User> findByEmail(String email) {
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM users WHERE email=?")) {
            s.setString(1,email);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(readUser(r)):Optional.empty();}
        }catch(SQLException e){throw new InfrastructureException("No se pudo consultar la cuenta",e);}
    }
    @Override public User save(User user) {
        String sql="MERGE INTO users (id,email,password_hash,role,active,created_at,last_login_at) KEY(id) VALUES (?,?,?,?,?,?,?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setObject(1,user.id().value());s.setString(2,user.email());s.setString(3,user.passwordHash());s.setString(4,user.role().name());s.setBoolean(5,user.active());s.setTimestamp(6,Timestamp.from(user.createdAt()));
            if(user.lastLoginAt()==null)s.setNull(7,Types.TIMESTAMP_WITH_TIMEZONE);else s.setTimestamp(7,Timestamp.from(user.lastLoginAt()));s.executeUpdate();return user;
        }catch(SQLException e){throw new InfrastructureException("No se pudo guardar la cuenta",e);}
    }
    @Override public void savePasswordReset(PasswordReset reset) {
        String sql="INSERT INTO password_resets(id,user_id,token_hash,expires_at,used_at) VALUES(?,?,?,?,?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setObject(1,reset.id().value());s.setObject(2,reset.userId().value());s.setString(3,reset.tokenHash());s.setTimestamp(4,Timestamp.from(reset.expiresAt()));
            if(reset.usedAt()==null)s.setNull(5,Types.TIMESTAMP_WITH_TIMEZONE);else s.setTimestamp(5,Timestamp.from(reset.usedAt()));s.executeUpdate();
        }catch(SQLException e){throw new InfrastructureException("No se pudo guardar la solicitud de recuperación",e);}
    }
    @Override public Optional<PasswordReset> findPasswordReset(String tokenHash) {
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM password_resets WHERE token_hash=?")) {
            s.setString(1,tokenHash);try(ResultSet r=s.executeQuery()){if(!r.next())return Optional.empty();Timestamp used=r.getTimestamp("used_at");return Optional.of(new PasswordReset(PasswordResetId.of(r.getObject("id",UUID.class)),UserId.of(r.getObject("user_id",UUID.class)),r.getString("token_hash"),r.getTimestamp("expires_at").toInstant(),used==null?null:used.toInstant()));}
        }catch(SQLException e){throw new InfrastructureException("No se pudo validar el token de recuperación",e);}
    }
    @Override public void saveAudit(AccessAudit audit) {
        String sql="INSERT INTO access_audit(id,user_id,action,ip_address,occurred_at,successful) VALUES(?,?,?,?,?,?)";
        try(Connection c=dataSource.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            s.setObject(1,audit.id().value());s.setObject(2,audit.userId()==null?null:audit.userId().value());s.setString(3,audit.action());s.setString(4,audit.ipAddress());s.setTimestamp(5,Timestamp.from(audit.occurredAt()));s.setBoolean(6,audit.successful());s.executeUpdate();
        }catch(SQLException e){throw new InfrastructureException("No se pudo registrar el evento de acceso",e);}
    }
    private static User readUser(ResultSet r)throws SQLException {
        Timestamp created=r.getTimestamp("created_at"),login=r.getTimestamp("last_login_at");
        return new User(UserId.of(r.getObject("id",UUID.class)),r.getString("email"),r.getString("password_hash"),Role.valueOf(r.getString("role")),r.getBoolean("active"),created.toInstant(),login==null?null:login.toInstant());
    }
}
