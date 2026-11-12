package co.ufps.clinica.application.identity;

import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.UserId;

public record AuthenticatedUser(UserId userId, String email, Role role) {}
