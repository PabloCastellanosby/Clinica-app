package co.ufps.clinica.api.security;

import java.lang.reflect.Method;

public final class RoutePermissionKey {
    private RoutePermissionKey() {}
    public static String forMethod(Class<?> resourceClass, Method method) {
        return resourceClass.getSimpleName() + "." + method.getName();
    }
}
