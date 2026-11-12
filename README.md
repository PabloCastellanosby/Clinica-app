# Backend de Clínica

Backend Maven multimódulo en Java 17 y Quarkus, organizado con Clean Architecture. Las dependencias apuntan hacia el centro: Entities no depende de otros módulos; Use Cases depende de Entities; Interface Adapters y Frameworks & Drivers dependen de Use Cases; API ensambla los módulos y expone REST.

```text
clinica-entities/             entidades y reglas de negocio
clinica-usecases/             casos de uso y puertos de salida
clinica-interface-adapters/   controladores, DTOs y presentadores
clinica-frameworks-drivers/   persistencia JPA, correo, PDF y seguridad
clinica-api/                  endpoints REST, autorización y composición
```

## Construcción y ejecución

Se requiere Maven y JDK 17 o 21. El `maven.compiler.release` del proyecto es 17.

```powershell
mvn clean test
mvn clean verify
mvn -pl clinica-api -am quarkus:dev
```

`clinica-api` inicia en el puerto 8080. El endpoint público de salud es `GET /api/health`. El resto de rutas requiere sesión Bearer salvo las marcadas expresamente como públicas (registro/login y recuperación).

Configura el primer administrador antes de iniciar el servicio:

```text
CLINICA_ADMIN_EMAIL=admin@tu-clinica.com
CLINICA_ADMIN_PASSWORD=<contraseña de entre 10 y 128 caracteres>
```

El bootstrap crea la cuenta si no existe y verifica que la cuenta configurada sea administradora. No configura ni reemplaza contraseñas de cuentas ya creadas.

## Persistencia y servicios externos

H2 se usa de forma predeterminada para desarrollo y guarda los datos en `./data/clinica`. La persistencia real del proyecto es MariaDB; configura `CLINICA_DB_KIND=mariadb`, `CLINICA_DB_URL=jdbc:mariadb://host:3306/clinica`, `CLINICA_DB_USERNAME` y `CLINICA_DB_PASSWORD` para conectarla.

Flyway aplica las migraciones versionadas al iniciar la API. Hay scripts para H2 y MariaDB bajo `clinica-api/src/main/resources/db/migration`; V1 crea 32 tablas, V2 agrega 44 claves foráneas, V3 impide registrar más de un encuentro clínico por cita, V4 exige especialidad para médicos y servicios, V5 crea el catálogo de permisos y las asignaciones por rol, V6 añade el control de intentos de entrega, V7 la marca temporal para recuperar envíos interrumpidos y V8 impide asociar más de una factura a la misma cita. Hibernate valida el esquema migrado y no lo altera automáticamente. Los esquemas existentes se baselinen en la versión 1 y reciben las ocho migraciones; revisa y respalda el esquema real antes de actualizar. V2 requiere relaciones válidas, V3 requiere eliminar encuentros duplicados por cita, V4 requiere asignar especialidades a médicos y servicios que tengan el campo vacío, y V8 requiere resolver cualquier duplicado existente de factura por cita antes de migrar.

El correo usa SMTP. Configura `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_SSL` y `CLINICA_MAIL_FROM`. Los mensajes se conservan en una bandeja de salida y se reintentan automáticamente cada minuto hasta cinco intentos; un envío que se interrumpa se recupera al pasar diez minutos. El enlace de recuperación usa `CLINICA_PASSWORD_RESET_URL`. El umbral de inventario se configura con `CLINICA_MINIMUM_STOCK`.

## Funcionalidad disponible

- **Identidad y acceso:** registro vinculado a un perfil de paciente, login, sesiones Bearer con expiración y cierre, recuperación de contraseña, bootstrap de administrador, alta y gestión de estado/rol del personal, permisos persistidos por rol y endpoint, administración de permisos por administrador y auditoría de accesos autenticados e intentos de login. El administrador es un rol de supervisión y sus permisos no se pueden revocar desde la API.

  - El administrador puede consultar la matriz con `GET /api/auth/permissions` y actualizar una asignación con `PUT /api/auth/roles/{role}/permissions/{permissionKey}?allowed=true|false`. Las claves de permiso se inicializan a partir de las rutas declaradas y las asignaciones existentes se conservan entre reinicios.
- **Clínica y agenda:** clínicas, sedes, consultorios, horarios de apertura, festivos, especialidades, catálogo de servicios, registro de médicos y disponibilidad, citas propias y de recepción, agenda, transiciones de estado, recordatorios por correo y lista de espera.
- **Pacientes:** registro, búsqueda, edición, perfil propio, validación de duplicados por documento y asociación de cuenta.
- **Atención clínica:** encuentros, signos vitales, alergias, antecedentes, órdenes de exámenes, recetas, archivos adjuntos y acceso a historias limitado al paciente, su médico relacionado y administradores.
- **Facturación e inventario:** facturas y líneas, estados de pago, medicamentos, lotes, movimientos de stock y alertas por bajo inventario o vencimiento.
- **Comunicaciones y reportes:** correos transaccionales, mensajes propios, comunicados por sede, dashboard, métricas por médico/especialidad, tasas de cancelación e inasistencia y exportación CSV. Las recetas se pueden descargar en PDF.

Los identificadores del dominio son value objects UUID tipados con `generate()` y `of(UUID)`. Las entidades JPA viven solo en Frameworks & Drivers; los casos de uso dependen de puertos de repositorio y no de JPA.

## Pruebas

La suite contiene 23 pruebas y cubre reglas de cancelación de citas, activación y roles de personal, visibilidad de historias clínicas, gestión de permisos, reintentos de correo, validación/exportación de reportes, cálculos y duplicados de facturación y generación de PDF. Se verificó el arranque integrado con JDK 21, las migraciones H2, Hibernate y la autorización dinámica por rol. Falta ejecutar las migraciones sobre una instancia MariaDB real.
