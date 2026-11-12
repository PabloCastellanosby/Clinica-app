package co.ufps.clinica.api.security;

import co.ufps.clinica.api.appointment.AppointmentResource;
import co.ufps.clinica.api.appointment.WaitlistResource;
import co.ufps.clinica.api.billing.InvoiceResource;
import co.ufps.clinica.api.catalog.CatalogResource;
import co.ufps.clinica.api.clinic.ClinicResource;
import co.ufps.clinica.api.clinicalrecord.ClinicalRecordResource;
import co.ufps.clinica.api.communication.CommunicationResource;
import co.ufps.clinica.api.identity.IdentityResource;
import co.ufps.clinica.api.identity.RolePermissionResource;
import co.ufps.clinica.api.inventory.InventoryResource;
import co.ufps.clinica.api.patient.PatientResource;
import co.ufps.clinica.api.reports.ReportsResource;
import co.ufps.clinica.api.staff.DoctorResource;
import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.security.Permission;
import co.ufps.clinica.domain.security.PermissionId;
import co.ufps.clinica.domain.security.RolePermissionGrant;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class RolePermissionSeeder {
    private final RolePermissionRepository repository;
    private static final List<Class<?>> RESOURCES = List.of(IdentityResource.class, PatientResource.class,
            DoctorResource.class, ClinicResource.class, CatalogResource.class, AppointmentResource.class,
            WaitlistResource.class, ClinicalRecordResource.class, InvoiceResource.class,
            CommunicationResource.class, InventoryResource.class, ReportsResource.class, RolePermissionResource.class);

    public RolePermissionSeeder(RolePermissionRepository repository) { this.repository = repository; }

    @Transactional
    void seed(@Observes StartupEvent event) {
        List<RolePermissionGrant> defaults = new java.util.ArrayList<>();
        for (Class<?> resource : RESOURCES) {
            RequireRoles classPolicy = resource.getAnnotation(RequireRoles.class);
            for (var method : resource.getDeclaredMethods()) {
                if (method.isSynthetic() || method.isBridge() || method.isAnnotationPresent(PublicEndpoint.class)) continue;
                RequireRoles policy = method.getAnnotation(RequireRoles.class);
                if (policy == null) policy = classPolicy;
                if (policy == null) continue;
                String key = RoutePermissionKey.forMethod(resource, method);
                Permission permission = new Permission(PermissionId.generate(), key,
                        resource.getSimpleName() + " · " + method.getName());
                for (Role role : Role.values()) {
                    boolean initiallyAllowed = List.of(policy.value()).contains(role);
                    defaults.add(new RolePermissionGrant(role, permission, initiallyAllowed));
                }
            }
        }
        repository.ensureDefaults(defaults);
    }
}
