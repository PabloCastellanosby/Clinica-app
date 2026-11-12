package co.ufps.clinica.adapters.identity;

import co.ufps.clinica.application.identity.AccountService;
import co.ufps.clinica.application.identity.AuthenticatedUser;
import co.ufps.clinica.application.identity.IssuedSession;
import co.ufps.clinica.application.identity.SessionService;
import co.ufps.clinica.application.identity.PasswordResetService;
import co.ufps.clinica.domain.identity.User;
import co.ufps.clinica.domain.identity.Role;
import java.util.Objects;

public final class IdentityController {
    private final AccountService accounts;
    private final SessionService sessions;
    private final PasswordResetService passwordResets;

    public IdentityController(AccountService accounts, SessionService sessions, PasswordResetService passwordResets) {
        this.accounts = Objects.requireNonNull(accounts);
        this.sessions = Objects.requireNonNull(sessions);
        this.passwordResets = Objects.requireNonNull(passwordResets);
    }

    public String registerPatient(RegisterAccountRequest request) {
        User user = accounts.registerPatient(request.email(), request.password(), request.documentType(), request.documentNumber());
        return user.id().value().toString();
    }

    public LoginResponse login(LoginRequest request, String ipAddress) {
        User user = accounts.authenticate(request.email(), request.password(), ipAddress);
        IssuedSession issued = sessions.issue(user);
        return new LoginResponse(issued.accessToken(), "Bearer", issued.expiresAt(),
                user.id().value().toString(), user.email(), user.role().name());
    }

    public void logout(String token) { sessions.revoke(token); }
    public String provisionStaff(AuthenticatedUser actor, StaffAccountRequest request) {
        Role role = Role.valueOf(request.role());
        User user = accounts.provisionStaff(actor.role(), request.email(), request.password(), role);
        return user.id().value().toString();
    }
    public User setStaffActive(AuthenticatedUser actor, String targetUserId, boolean active) {
        return accounts.setStaffActive(actor.role(), actor.userId(),
                co.ufps.clinica.domain.identity.UserId.of(java.util.UUID.fromString(targetUserId)), active);
    }
    public User changeStaffRole(AuthenticatedUser actor, String targetUserId, String role) {
        Role selectedRole = Role.valueOf(role);
        return accounts.changeStaffRole(actor.role(), actor.userId(),
                co.ufps.clinica.domain.identity.UserId.of(java.util.UUID.fromString(targetUserId)), selectedRole);
    }
    public void requestPasswordReset(PasswordResetRequest request) { passwordResets.request(request.email()); }
    public void confirmPasswordReset(ConfirmPasswordResetRequest request) { passwordResets.reset(request.token(), request.newPassword()); }
}
