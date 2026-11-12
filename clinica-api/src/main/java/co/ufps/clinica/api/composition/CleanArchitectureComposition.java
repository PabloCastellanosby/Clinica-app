package co.ufps.clinica.api.composition;

import co.ufps.clinica.adapters.patient.PatientController;
import co.ufps.clinica.adapters.billing.BillingController;
import co.ufps.clinica.adapters.catalog.CatalogController;
import co.ufps.clinica.adapters.clinic.ClinicController;
import co.ufps.clinica.adapters.clinicalrecord.ClinicalRecordController;
import co.ufps.clinica.adapters.communication.CommunicationController;
import co.ufps.clinica.adapters.inventory.InventoryController;
import co.ufps.clinica.adapters.staff.DoctorController;
import co.ufps.clinica.adapters.reports.ReportsController;
import co.ufps.clinica.application.patient.PatientRegistry;
import co.ufps.clinica.application.billing.InvoiceService;
import co.ufps.clinica.application.catalog.CatalogService;
import co.ufps.clinica.application.clinic.ClinicService;
import co.ufps.clinica.application.clinicalrecord.ClinicalRecordService;
import co.ufps.clinica.application.communication.CommunicationService;
import co.ufps.clinica.application.inventory.InventoryService;
import co.ufps.clinica.application.staff.DoctorService;
import co.ufps.clinica.application.identity.AccountService;
import co.ufps.clinica.application.identity.SessionService;
import co.ufps.clinica.application.identity.PasswordResetService;
import co.ufps.clinica.adapters.identity.IdentityController;
import co.ufps.clinica.adapters.appointment.AppointmentController;
import co.ufps.clinica.adapters.appointment.WaitlistController;
import co.ufps.clinica.adapters.security.PermissionController;
import co.ufps.clinica.application.appointment.AppointmentScheduler;
import co.ufps.clinica.application.appointment.WaitlistService;
import co.ufps.clinica.application.appointment.ReminderService;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.application.port.out.appointment.WaitlistRepository;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.application.port.out.communication.EmailGateway;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import co.ufps.clinica.application.port.out.clinic.ClinicRepository;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import co.ufps.clinica.application.port.out.inventory.InventoryRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import co.ufps.clinica.application.reports.ReportQueries;
import co.ufps.clinica.application.reports.ReportService;
import co.ufps.clinica.application.security.PermissionService;
import co.ufps.clinica.application.port.out.security.RolePermissionRepository;
import co.ufps.clinica.application.documents.DocumentGenerator;
import co.ufps.clinica.application.documents.PrescriptionDocumentService;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/** Punto externo de composición: conecta adaptador JPA, caso de uso y controlador. */
@ApplicationScoped
public class CleanArchitectureComposition {
    @Produces @Dependent AppointmentScheduler appointmentScheduler(AppointmentRepository appointments, PatientRepository patients,
            DoctorRepository doctors, ClinicRepository clinics, CatalogRepository catalog, CommunicationService notifications,
            WaitlistService waitlist) {
        return new AppointmentScheduler(appointments, patients, doctors, clinics, catalog, notifications, waitlist);
    }
    @Produces @Dependent ReminderService reminderService(AppointmentRepository appointments, CommunicationService notifications) {
        return new ReminderService(appointments, notifications);
    }
    @Produces @Dependent AppointmentController appointmentController(AppointmentScheduler scheduler) { return new AppointmentController(scheduler); }
    @Produces @Dependent WaitlistService waitlistService(WaitlistRepository waitlist, PatientRepository patients,
            DoctorRepository doctors, CatalogRepository catalog, CommunicationService notifications) {
        return new WaitlistService(waitlist, patients, doctors, catalog, notifications);
    }
    @Produces @Dependent WaitlistController waitlistController(WaitlistService service) { return new WaitlistController(service); }
    @Produces @Dependent AccountService accountService(UserRepository repository, PatientRepository patients, AccountService.Passwords passwords) { return new AccountService(repository, patients, passwords); }
    @Produces @Dependent SessionService sessionService(UserRepository repository) { return new SessionService(repository); }
    @Produces @Dependent PasswordResetService passwordResetService(UserRepository repository, AccountService.Passwords passwords,
            EmailGateway emailGateway, @ConfigProperty(name = "clinica.password-reset.url", defaultValue = "http://localhost:3000/reset-password") String resetUrl) {
        return new PasswordResetService(repository, passwords, emailGateway, resetUrl);
    }
    @Produces @Dependent IdentityController identityController(AccountService accounts, SessionService sessions, PasswordResetService resets) {
        return new IdentityController(accounts, sessions, resets);
    }
    @Produces @Dependent PatientRegistry patientRegistry(PatientRepository repository) { return new PatientRegistry(repository); }
    @Produces @Dependent PatientController patientController(PatientRegistry patientRegistry) { return new PatientController(patientRegistry); }

    @Produces @Dependent InvoiceService invoiceService(InvoiceRepository repository, PatientRepository patients,
            AppointmentRepository appointments) { return new InvoiceService(repository, patients, appointments); }
    @Produces @Dependent BillingController billingController(InvoiceService invoiceService) { return new BillingController(invoiceService); }

    @Produces @Dependent CatalogService catalogService(CatalogRepository repository) { return new CatalogService(repository); }
    @Produces @Dependent CatalogController catalogController(CatalogService catalogService) { return new CatalogController(catalogService); }

    @Produces @Dependent ClinicService clinicService(ClinicRepository repository) { return new ClinicService(repository); }
    @Produces @Dependent ClinicController clinicController(ClinicService clinicService) { return new ClinicController(clinicService); }

    @Produces @Dependent ClinicalRecordService clinicalRecordService(ClinicalRecordRepository repository,
            AppointmentRepository appointments, DoctorRepository doctors, PatientRepository patients) {
        return new ClinicalRecordService(repository, appointments, doctors, patients);
    }
    @Produces @Dependent PrescriptionDocumentService prescriptionDocumentService(ClinicalRecordService records, DocumentGenerator generator) {
        return new PrescriptionDocumentService(records, generator);
    }
    @Produces @Dependent ClinicalRecordController clinicalRecordController(ClinicalRecordService clinicalRecordService,
            PatientRepository patients, PrescriptionDocumentService documents) {
        return new ClinicalRecordController(clinicalRecordService, patients, documents);
    }

    @Produces @Dependent CommunicationService communicationService(CommunicationRepository repository, EmailGateway emailGateway,
            PatientRepository patients) { return new CommunicationService(repository, emailGateway, patients); }
    @Produces @Dependent CommunicationController communicationController(CommunicationService communicationService) { return new CommunicationController(communicationService); }

    @Produces @Dependent InventoryService inventoryService(InventoryRepository repository,
            @ConfigProperty(name = "clinica.inventory.minimum-stock", defaultValue = "10") int minimumStock) {
        return new InventoryService(repository, minimumStock);
    }
    @Produces @Dependent InventoryController inventoryController(InventoryService inventoryService) { return new InventoryController(inventoryService); }

    @Produces @Dependent DoctorService doctorService(DoctorRepository repository, UserRepository users,
            CatalogRepository catalog) { return new DoctorService(repository, users, catalog); }
    @Produces @Dependent DoctorController doctorController(DoctorService doctorService) { return new DoctorController(doctorService); }
    @Produces @Dependent ReportService reportService(ReportQueries queries) { return new ReportService(queries); }
    @Produces @Dependent ReportsController reportsController(ReportService service) { return new ReportsController(service); }
    @Produces @Dependent PermissionService permissionService(RolePermissionRepository repository) { return new PermissionService(repository); }
    @Produces @Dependent PermissionController permissionController(PermissionService service) { return new PermissionController(service); }
}
