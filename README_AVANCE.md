# AgroDirecto API — Avance

API REST construida con Spring Boot (Java 21), siguiendo la arquitectura `Controller → Service → Repository → Entity`, con DTOs simples para los contratos que no coinciden con las entidades, manejo básico de errores HTTP y seguridad JWT basada en roles.

## Cómo correrlo
1. En PostgreSQL crear la base: `CREATE DATABASE agrodirecto;` y ejecutar sobre ella `src/main/resources/db/agrodirecto_schema_postgresql.sql` (crea las 11 tablas).
2. Editar `src/main/resources/application.properties` con tu usuario/contraseña de PostgreSQL.
3. `mvn spring-boot:run` (puerto 8088) o ejecutar `AgroDirectoApiApplication` desde IntelliJ IDEA.
4. Swagger UI: http://localhost:8088/swagger-ui/index.html

Dependencias: Spring Web, Spring Data JPA, JDBC API, PostgreSQL Driver, Validation, SpringDoc OpenAPI, DevTools (+ Lombok).

`spring.jpa.hibernate.ddl-auto=none` a propósito: las tablas ya las crea el script, no Hibernate (a diferencia de la PC1, que usaba `update` porque sus tablas eran más simples y no tenían CHECK compuestos).

## Cobertura: 38 de 38 endpoints del documento (100% de rutas implementadas)

| # | Endpoint | Controlador | Estado |
|---|----------|--------------|--------|
| EP01 | POST /api/v1/auth/registro-productor | AuthController | ✅ |
| EP02 | POST /api/v1/auth/registro-transportista | AuthController | ✅ |
| EP03 | GET /api/v1/cultivos | CultivoController | ✅ |
| EP04 | POST /api/v1/cargas | CargaController | ✅ |
| EP05 | PATCH /api/v1/cargas/{id}/cancelar | CargaController | ✅ |
| EP06 | GET /api/v1/transportistas/{id}/lotes | TransportistaController | ✅ |
| EP07 | POST /api/v1/calificaciones | CalificacionController | ✅ |
| EP08 | GET /api/v1/calificaciones/lote/{id} | CalificacionController | ✅ |
| EP09 | GET /api/v1/usuarios | UsuarioController | ✅ |
| EP10 | PATCH /api/v1/usuarios/{id}/estado | UsuarioController | ✅ |
| EP11 | GET /api/v1/chats/solicitudes | ChatController | ✅ |
| EP12 | PUT /api/v1/chats/solicitudes/{id}/estado | ChatController | ✅ |
| EP13 | POST /api/v1/chats/solicitudes/{id}/mensajes | ChatController | ✅ |
| EP14 | GET /api/v1/precios-mercado | PrecioMercadoController | ✅ |
| EP15 | GET /api/v1/precios-mercado/ultimo/{id} | PrecioMercadoController | ✅ |
| EP16 | GET /api/v1/notificaciones/usuario/{id} | NotificacionController | ✅ |
| EP17 | PATCH /api/v1/notificaciones/{id}/leer | NotificacionController | ✅ |
| EP18 | GET /api/v1/transacciones | TransaccionController | ✅ |
| EP19 | GET /api/v1/reportes/transacciones/resumen | ReporteController | ✅ |
| EP20 | GET /api/v1/cargas?estado=... | CargaController | ✅ |
| EP21 | GET /api/v1/lotes/{id} | LoteController | ✅ |
| EP22 | GET /api/v1/productores/{id}/saldo | UsuarioController | ✅ |
| EP23 | GET /api/v1/lotes/{id}/financiero | LoteController | ✅ |
| EP24 | GET /api/v1/reportes/ahorro-productor/{id} | ReporteController | ✅ |
| EP25 | GET /api/v1/cargas/retorno | CargaController | ✅ |
| EP26 | PATCH /api/v1/cargas/{id}/asignar-retorno | CargaController | ✅ |
| EP27 | GET /api/v1/usuarios/{id}/transportista | UsuarioController | ✅ |
| EP28 | GET /api/v1/reportes/reputacion-transportista/{id} | ReporteController | ✅ |
| EP29 | GET /api/v1/usuarios/{id}/administrador | UsuarioController | ✅ |
| EP30 | PATCH /api/v1/lotes/{id}/confirmar-entrega | LoteController | ✅ |
| EP31 | GET /api/v1/lotes/{id}/seguimiento | LoteController | ✅ |
| EP32 | PATCH /api/v1/lotes/{id}/reportar-retraso | LoteController | ✅ |
| EP33 | GET /api/v1/productores/{id}/lotes | UsuarioController | ✅ |
| EP34 | GET /api/v1/productores/{id}/resumen | UsuarioController | ✅ |
| EP35 | POST /api/v1/lotes/{id}/gastos | GastoViajeController | ✅ |
| EP36 | GET /api/v1/lotes/{id}/gastos | GastoViajeController | ✅ |
| EP37 | POST /api/v1/usuarios/{id}/suscripcion-premium | SuscripcionController | ✅ |
| EP38 | GET /api/v1/usuarios/{id}/suscripcion-premium | SuscripcionController | ✅ |

### Reportes añadidos en esta versión
- **EP19:** total transado y comisión acumulada por mes, tomando lotes con `estadoPago` no nulo.
- **EP24:** estima el ahorro por carga comparando `tarifaPropuesta` con la parte proporcional del flete consolidado según el peso. El esquema no tiene una columna independiente para una cotización individual real, por lo que se usa `tarifaPropuesta` como referencia.
- **EP28:** calcula el promedio de estrellas y devuelve hasta 10 comentarios recientes; si no hay calificaciones, `promedioEstrellas` se devuelve como `null` y `totalResenas` como `0`.
- **EP34:** calcula lotes entregados e ingreso neto acumulado del mes actual, contando cada lote una sola vez.

## Decisiones de diseño (para explicar en la sustentación)
- **DTOs (records) solo donde el contrato del endpoint lo exige**: la PC1 de ejemplo no usa DTOs porque sus formularios son casi idénticos a la entidad. Aquí sí hicieron falta en registro, cambios de estado y algunos POST, porque el documento de endpoints define un JSON distinto al de la entidad completa (por ejemplo, el registro no manda el objeto `rol` completo, solo se infiere del path).
- **Identidad del usuario:** las operaciones sensibles usan el usuario autenticado por JWT, no un `idUsuario` libre enviado por el cliente.
- **Errores HTTP:** `ApiExceptionHandler` traduce errores de validación (400), permisos (403), conflictos (409) y recursos inexistentes (404) a JSON.
- **`ddl-auto=none`**: la única diferencia de configuración real frente a la PC1, y es obligatoria porque el script SQL ya trae CHECK compuestos y los índices filtrados que Hibernate no podría generar solo.

## Seguridad (JWT)

La API usa Spring Security + JWT. El registro está en `/api/v1/auth/registro-productor` y `/api/v1/auth/registro-transportista`; el login está en `POST /api/v1/auth/login`.

El login recibe `correo` y `contrasena` y devuelve un Bearer Token JWT. Para endpoints protegidos, enviar:

`Authorization: Bearer <token>`

La contraseña se almacena con BCrypt. `Usuario` y `Rol` son las entidades existentes del proyecto; no se duplican dentro de `security`. Las operaciones de chat, notificaciones, gastos, entrega, cancelación y suscripción verifican propiedad/participación cuando corresponde. Las operaciones de chat, notificaciones, gastos, entrega, cancelación y suscripción verifican propiedad/participación cuando corresponde.

Para AWS/Docker configurar las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` y opcionalmente `JWT_EXPIRATION_MS`.


**Nota de validación de entrega:** el esquema actual no tiene una columna independiente para el QR; por ello el código manual/QR enviado se valida contra `codigoLote`. Si se necesita un QR con token aleatorio, habrá que agregar y poblar esa columna en la base de datos.


