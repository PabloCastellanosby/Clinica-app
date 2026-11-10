# Clinica Backend

Backend Maven multimódulo en Java 17, organizado según las capas de Clean Architecture de Robert C. Martin.

```text
clinica-entities/             entidades empresariales, reglas e identificadores
clinica-usecases/             casos de uso y puertos de salida
clinica-interface-adapters/   controladores, DTOs y presentadores
clinica-frameworks-drivers/   persistencia JPA, entidades SQL y adaptadores técnicos
clinica-api/                  entrada REST y composición de dependencias
```

Las dependencias apuntan hacia el centro: `Frameworks & Drivers` y `Interface Adapters` dependen de `Use Cases`; `Use Cases` depende de `Entities`. Los puertos de repositorio viven en Use Cases y los adaptadores JPA exteriores los implementan. Dentro de cada módulo, las clases se agrupan por funcionalidad.

El dominio empresarial y los casos de uso son Java puro; JAX-RS, CDI y JPA están confinados a los módulos exteriores. La API actúa como punto de composición y traduce errores de dominio/aplicación a respuestas HTTP.

Cada módulo mantiene su propio `pom.xml` y `src/main`; Maven crea un `target/` en cada módulo al compilar. Desde la raíz, `mvn clean compile` verifica todo el reactor. Para iniciar el servicio en IntelliJ, ejecuta `quarkus:dev` en el módulo `clinica-api`. El perfil local usa H2 en memoria; los endpoints de salud y pacientes empiezan en `/api/health` y `/api/patients`.

Los puertos `PatientRepository`, `AppointmentRepository` y `UserRepository` viven en Use Cases. `PatientRepositoryJpa`, `AppointmentRepositoryJpa` y `UserRepositoryJpa` implementan esos puertos en Frameworks & Drivers y mapean los modelos mediante entidades `*Jpa`. Hibernate ORM administra el esquema local con `quarkus.hibernate-orm.database.generation=update`; en producción conviene sustituir esa opción por migraciones versionadas.

Las opciones cerradas se definen en enums independientes dentro de `clinica-entities`: `Role`, `StaffStatus`, `AppointmentStatus`, `ExamOrderStatus`, `InvoiceStatus`, `StockMovementType`, `MessageStatus` y `NotificationChannel`. `AppointmentStatus` también contiene la regla para validar transiciones de estado. Las claves y referencias del dominio usan value objects tipados (`UserId`, `PatientId`, `AppointmentId`, etc.), cada uno con `generate()` y `of(UUID)`; los adaptadores JPA convierten esos tipos al UUID que almacena la base.

Las excepciones se separan por módulo (`DomainException`, `ApplicationException`, `InfrastructureException`, `ApiException`) y también hay errores específicos como `PatientAlreadyExistsException` y `PatientNotFoundException`. Los modelos del resto de áreas clínicas están definidos; sus entidades JPA y repositorios se pueden incorporar por funcionalidad.
