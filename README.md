# Clinica Backend

Backend Maven multimódulo en Java 17, siguiendo las capas del proyecto Streamflix.

```text
clinica-domain/          modelos, reglas e interfaces Repository
clinica-application/     casos de uso y excepciones de aplicación
clinica-infrastructure/  adaptadores JDBC, esquema y excepciones técnicas
clinica-api/             recursos REST, configuración y errores HTTP
```

La dirección de dependencias es `clinica-api -> clinica-application -> clinica-domain` y `clinica-api -> clinica-infrastructure -> clinica-domain`. El dominio y la aplicación son Java puro; REST, CDI y JDBC están confinados a API e infraestructura. La API actúa como punto de composición y traduce errores de aplicación/dominio a respuestas HTTP.

Cada módulo mantiene su propio `pom.xml` y `src/main`; Maven crea un `target/` en cada módulo al compilar. Ejecuta desde la raíz `mvn clean install` y luego `mvn -pl clinica-api -am quarkus:dev`. El perfil local usa H2 en memoria y el endpoint de salud es `GET /api/health`.

Los puertos `PatientRepository`, `AppointmentRepository` y `UserRepository` viven en dominio. `PatientJdbcRepository`, `AppointmentJdbcRepository` y `UserJdbcRepository` implementan acceso JDBC; el inicializador crea las tablas necesarias para esos adaptadores. La creación del esquema es provisional para desarrollo; el siguiente paso de despliegue debe reemplazarla por migraciones versionadas.

Las opciones cerradas se definen en enums independientes dentro de `clinica-domain`: `Role`, `StaffStatus`, `AppointmentStatus`, `ExamOrderStatus`, `InvoiceStatus`, `StockMovementType`, `MessageStatus` y `NotificationChannel`. `AppointmentStatus` también contiene la regla para validar transiciones de estado.

Las excepciones se separan por módulo (`DomainException`, `ApplicationException`, `InfrastructureException`, `ApiException`) y también hay errores específicos como `PatientAlreadyExistsException` y `PatientNotFoundException`. Los módulos contienen modelos para el resto de áreas solicitadas, pero sus repositorios y adaptadores JDBC todavía deben añadirse.
