package co.ufps.clinica.api.security;

import co.ufps.clinica.application.identity.AuthenticatedUser;
import java.security.Principal;

public record RequestPrincipal(AuthenticatedUser user) implements Principal {
    @Override public String getName() { return user.userId().value().toString(); }
}
