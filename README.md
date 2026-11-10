# Clinica Backend

Backend Maven multimódulo en Java 17, siguiendo las capas del proyecto Streamflix.

```text
clinica-domain/          modelos, reglas e interfaces Repository
clinica-application/     casos de uso y excepciones de aplicación
clinica-infrastructure/  entidades JPA, repositorios y excepciones técnicas
clinica-api/             recursos REST, configuración y errores HTTP
```

Dentro de cada módulo, las clases están agrupadas en paquetes por funcionalidad. Por ejemplo, `clinica-domain/.../domain/patient` reúne `Patient`, `PatientId`, `PatientRepository` y su excepción; `domain/appointment` reúne la cita, estado, identificador, lista de espera y repositorio. Aplicación separa casos de uso por flujo, infraestructura separa persistencia por agregado y API separa salud y manejo de errores.

La dirección de dependencias es `clinica-api -> clinica-application -> clinica-domain` y `clinica-api -> clinica-infrastructure -> clinica-domain`. El dominio y la aplicación son Java puro; REST, CDI y JPA están confinados a API e infraestructura. La API actúa como punto de composición y traduce errores de aplicación/dominio a respuestas HTTP.

Cada módulo mantiene su propio `pom.xml` y `src/main`; Maven crea un `target/` en cada módulo al compilar. Ejecuta desde la raíz `mvn clean install` y luego `mvn -pl clinica-api -am quarkus:dev`. El perfil local usa H2 en memoria y el endpoint de salud es `GET /api/health`.

Los puertos `PatientRepository`, `AppointmentRepository` y `UserRepository` viven junto a sus agregados en dominio. `PatientRepositoryJpa`, `AppointmentRepositoryJpa` y `UserRepositoryJpa` implementan esos puertos en infraestructura y mapean los modelos mediante entidades `*Jpa`. Hibernate ORM administra el esquema local con `quarkus.hibernate-orm.database.generation=update`; en producción conviene sustituir esa opción por migraciones versionadas.

Las opciones cerradas se definen en enums independientes dentro de `clinica-domain`: `Role`, `StaffStatus`, `AppointmentStatus`, `ExamOrderStatus`, `InvoiceStatus`, `StockMovementType`, `MessageStatus` y `NotificationChannel`. `AppointmentStatus` también contiene la regla para validar transiciones de estado. Las claves y referencias del dominio usan value objects tipados (`UserId`, `PatientId`, `AppointmentId`, etc.), cada uno con `generate()` y `of(UUID)`; los adaptadores JPA convierten esos tipos al UUID que almacena la base. Las altas de usuarios, pacientes y citas obtienen su clave desde el tipo `...Id` correspondiente.

Las excepciones se separan por módulo (`DomainException`, `ApplicationException`, `InfrastructureException`, `ApiException`) y también hay errores específicos como `PatientAlreadyExistsException` y `PatientNotFoundException`. Los modelos del resto de áreas clínicas están definidos; sus entidades JPA y repositorios se pueden incorporar por funcionalidad.
