package co.ufps.clinica.api.composition;

import co.ufps.clinica.adapters.patient.PatientController;
import co.ufps.clinica.adapters.billing.BillingController;
import co.ufps.clinica.adapters.catalog.CatalogController;
import co.ufps.clinica.adapters.clinic.ClinicController;
import co.ufps.clinica.adapters.clinicalrecord.ClinicalRecordController;
import co.ufps.clinica.adapters.communication.CommunicationController;
import co.ufps.clinica.adapters.inventory.InventoryController;
import co.ufps.clinica.adapters.staff.DoctorController;
import co.ufps.clinica.application.patient.PatientRegistry;
import co.ufps.clinica.application.billing.InvoiceService;
import co.ufps.clinica.application.catalog.CatalogService;
import co.ufps.clinica.application.clinic.ClinicService;
import co.ufps.clinica.application.clinicalrecord.ClinicalRecordService;
import co.ufps.clinica.application.communication.CommunicationService;
import co.ufps.clinica.application.inventory.InventoryService;
import co.ufps.clinica.application.staff.DoctorService;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.application.port.out.billing.InvoiceRepository;
import co.ufps.clinica.application.port.out.catalog.CatalogRepository;
import co.ufps.clinica.application.port.out.clinic.ClinicRepository;
import co.ufps.clinica.application.port.out.clinicalrecord.ClinicalRecordRepository;
import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import co.ufps.clinica.application.port.out.inventory.InventoryRepository;
import co.ufps.clinica.application.port.out.staff.DoctorRepository;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/** Punto externo de composición: conecta adaptador JPA, caso de uso y controlador. */
@ApplicationScoped
public class CleanArchitectureComposition {
    @Produces @Dependent PatientRegistry patientRegistry(PatientRepository repository) { return new PatientRegistry(repository); }
    @Produces @Dependent PatientController patientController(PatientRegistry patientRegistry) { return new PatientController(patientRegistry); }

    @Produces @Dependent InvoiceService invoiceService(InvoiceRepository repository) { return new InvoiceService(repository); }
    @Produces @Dependent BillingController billingController(InvoiceService invoiceService) { return new BillingController(invoiceService); }

    @Produces @Dependent CatalogService catalogService(CatalogRepository repository) { return new CatalogService(repository); }
    @Produces @Dependent CatalogController catalogController(CatalogService catalogService) { return new CatalogController(catalogService); }

    @Produces @Dependent ClinicService clinicService(ClinicRepository repository) { return new ClinicService(repository); }
    @Produces @Dependent ClinicController clinicController(ClinicService clinicService) { return new ClinicController(clinicService); }

    @Produces @Dependent ClinicalRecordService clinicalRecordService(ClinicalRecordRepository repository) { return new ClinicalRecordService(repository); }
    @Produces @Dependent ClinicalRecordController clinicalRecordController(ClinicalRecordService clinicalRecordService) { return new ClinicalRecordController(clinicalRecordService); }

    @Produces @Dependent CommunicationService communicationService(CommunicationRepository repository) { return new CommunicationService(repository); }
    @Produces @Dependent CommunicationController communicationController(CommunicationService communicationService) { return new CommunicationController(communicationService); }

    @Produces @Dependent InventoryService inventoryService(InventoryRepository repository) { return new InventoryService(repository); }
    @Produces @Dependent InventoryController inventoryController(InventoryService inventoryService) { return new InventoryController(inventoryService); }

    @Produces @Dependent DoctorService doctorService(DoctorRepository repository) { return new DoctorService(repository); }
    @Produces @Dependent DoctorController doctorController(DoctorService doctorService) { return new DoctorController(doctorService); }
}