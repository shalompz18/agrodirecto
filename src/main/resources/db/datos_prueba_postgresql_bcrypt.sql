/* ============================================================
   AgroDirecto — Datos de prueba (PostgreSQL)
   Para ejecutar DESPUES de agrodirecto_schema_postgresql.sql

   Uso: conectarte a la base "agrodirecto" y correr este script
   completo. Es seguro volver a correrlo las veces que quieras:
   el TRUNCATE del inicio deja las 11 tablas vacias y reinicia
   los IDENTITY, para que los ID siempre te den 1,2,3... en el
   mismo orden (eso es lo que permite escribir los FK como
   numeros fijos mas abajo, sin tener que adivinar nada).

   Cobertura pensada para poder probar en Swagger el 80% de
   endpoints ya implementados: login/registro, publicar carga,
   listar/filtrar, aprobar chat + mandar mensaje, calificar,
   confirmar entrega QR, reportar retraso, saldo, gastos de
   viaje y suscripcion premium.
   ============================================================ */

TRUNCATE TABLE
    gasto_viaje, notificacion, precio_mercado, calificacion,
    mensaje_chat, solicitud_chat, carga, lote, usuario, cultivo, rol
RESTART IDENTITY CASCADE;


/* ------------------------------------------------------------
   1. ROL — catalogo fijo (solo puede haber estos 3, por el CHECK)
   ------------------------------------------------------------ */
INSERT INTO rol (nombre_rol) VALUES
    ('productor'),       -- id_rol = 1
    ('transportista'),   -- id_rol = 2
    ('administrador');   -- id_rol = 3


/* ------------------------------------------------------------
   2. CULTIVO — 5 filas (el catalogo base de tu proyecto)
   ------------------------------------------------------------ */
INSERT INTO cultivo (nombre_cultivo) VALUES
    ('Papa blanca'),   -- id_cultivo = 1
    ('Cebolla roja'),  -- id_cultivo = 2
    ('Zanahoria'),     -- id_cultivo = 3
    ('Maiz'),          -- id_cultivo = 4
    ('Camote');        -- id_cultivo = 5


/* ------------------------------------------------------------
   3. USUARIO
   Nota: "usuario" en realidad guarda 3 roles distintos, asi
   que en vez de 5 filas en total te doy 5 productores + 5
   transportistas + 3 administradores (13 filas), para que
   puedas probar TODOS los endpoints de cada rol en Swagger,
   no solo un usuario suelto de cada uno.
   Contraseñas guardadas como hash BCrypt (no en texto plano),
   porque el proyecto ya tiene Spring Security + BCryptPasswordEncoder.
   El hash de abajo corresponde a la contraseña real "Agro2026*"
   para los 13 usuarios: usa esa misma contraseña al probar
   POST /api/v1/auth/login en Swagger con cualquiera de los
   correos de este script.
   ------------------------------------------------------------ */

-- Productores (id_usuario 1 a 5) — id_rol = 1
INSERT INTO usuario (nombre_completo, correo, contrasena, id_rol, estado_cuenta, dni, distrito) VALUES
    ('Juan Perez Lopez',        'juan.perez@agrodirecto.pe',     '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 1, 'verificado', '45678912', 'La Molina'),
    ('Maria Fernanda Torres',   'maria.torres@agrodirecto.pe',   '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 1, 'verificado', '41234567', 'Huancayo'),
    ('Roberto Sanchez Vega',    'roberto.sanchez@agrodirecto.pe','$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 1, 'verificado', '47890123', 'Ica'),
    ('Lucia Mendoza Ruiz',      'lucia.mendoza@agrodirecto.pe',  '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 1, 'pendiente',  '42345678', 'Canete'),   -- para probar EP10 (verificar)
    ('Andres Castillo Paredes', 'andres.castillo@agrodirecto.pe','$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 1, 'bloqueado',  '48901234', 'Chincha');  -- para probar EP10 (desbloquear)

-- Transportistas (id_usuario 6 a 10) — id_rol = 2
INSERT INTO usuario (nombre_completo, correo, contrasena, id_rol, estado_cuenta, ruc, placa, capacidad_toneladas, licencia, premium_hasta) VALUES
    ('Carlos Ramirez Soto',   'carlos.ramirez@agrodirecto.pe','$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 2, 'verificado', '20123456781', 'ABC-123', 8.5,  'A-IIb',  CURRENT_TIMESTAMP + INTERVAL '20 days'), -- premium activo, para EP38
    ('Miguel Angel Quispe',   'miguel.quispe@agrodirecto.pe', '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 2, 'verificado', '20234567892', 'XYZ-789', 12.0, 'A-IIIc', NULL),
    ('Fernando Huaman Rojas', 'fernando.huaman@agrodirecto.pe','$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS',2, 'verificado', '20345678903', 'DEF-456', 6.0,  'A-IIb',  NULL),
    ('Diego Alarcon Vidal',   'diego.alarcon@agrodirecto.pe',  '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS',2, 'verificado', '20456789014', 'GHI-321', 15.0, 'A-IIIc', NULL),
    ('Jose Luis Ochoa',       'jose.ochoa@agrodirecto.pe',     '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS',2, 'pendiente',  '20567890125', 'JKL-654', 9.5,  'A-IIb',  CURRENT_TIMESTAMP - INTERVAL '5 days'); -- premium vencido, para EP38

-- Administradores (id_usuario 11 a 13) — id_rol = 3
INSERT INTO usuario (nombre_completo, correo, contrasena, id_rol, estado_cuenta, ultimo_acceso) VALUES
    ('Gadiel Rojas Medina', 'gadiel.rojas@agrodirecto.pe', '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 3, 'verificado', CURRENT_TIMESTAMP - INTERVAL '1 hour'),
    ('Ana Lucia Vargas',    'ana.vargas@agrodirecto.pe',   '$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 3, 'verificado', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('Renzo Delgado Paz',   'renzo.delgado@agrodirecto.pe','$2a$10$fpn1xKbgxJq6uVFuccY.CeUEjqNExRVzwlsiC/QEcJnbmTC0uurVS', 3, 'verificado', NULL);


/* ------------------------------------------------------------
   4. LOTE — 5 filas, una por cada estado_lote posible
   id_transportista: 6=Carlos, 7=Miguel, 8=Fernando, 9=Diego
   ------------------------------------------------------------ */
INSERT INTO lote (codigo_lote, id_transportista, estado_lote, origen, destino, fecha_salida, fecha_llegada, flete_total,
                   porcentaje_comision, monto_comision, monto_neto, estado_pago, fecha_transaccion) VALUES
    ('L-2026-001', NULL, 'formandose',  NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),                                             -- id_lote 1: aun juntando cargas
    ('L-2026-002', 6,    'confirmado',  'La Molina, Lima', 'Mercado Mayorista N1, Lima', CURRENT_TIMESTAMP + INTERVAL '2 days', NULL, 850.00, NULL, NULL, NULL, NULL, NULL), -- id_lote 2
    ('L-2026-003', 7,    'en_transito', 'Huancayo, Junin',  'Mercado Mayorista N1, Lima', CURRENT_TIMESTAMP - INTERVAL '1 day', NULL, 1200.00, NULL, NULL, NULL, NULL, NULL), -- id_lote 3
    ('L-2026-004', 8,    'con_retraso', 'Ica, Ica',         'Mercado Mayorista N1, Lima', CURRENT_TIMESTAMP - INTERVAL '2 days', NULL, 950.00, NULL, NULL, NULL, NULL, NULL), -- id_lote 4
    ('L-2026-005', 9,    'entregado',   'Canete, Lima',     'Mercado Mayorista N1, Lima', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 2000.00, 5.00, 100.00, 1900.00, 'pagado', CURRENT_TIMESTAMP - INTERVAL '1 day'); -- id_lote 5: entregado y pagado


/* ------------------------------------------------------------
   5. CARGA — 10 filas (mas de 5 a proposito: se necesitan varias
   cargas de productores distintos dentro del mismo lote entregado
   para que las 5 calificaciones de mas abajo tengan sentido).
   id_productor: 1..5 | id_cultivo: 1..5 | id_lote: 1..5 o NULL
   ------------------------------------------------------------ */
INSERT INTO carga (codigo_carga, id_productor, id_cultivo, id_lote, peso_kg, tipo_carga, direccion_destino, tarifa_propuesta, fecha_recojo, es_urgente, estado_carga) VALUES
    ('C-2026-001', 1, 1, NULL, 500.00, 'perecible', 'Mercado Mayorista N1, Lima', 800.00, CURRENT_DATE + 3, FALSE, 'publicada'),        -- disponible, sin lote todavia
    ('C-2026-002', 2, 2, NULL, 300.00, 'seco',      'Mercado Mayorista N1, Lima', 450.00, CURRENT_DATE + 2, TRUE,  'publicada'),        -- tipo "seco" y sin lote -> aparece en EP25 (cargas de retorno)
    ('C-2026-003', 3, 3, NULL, 200.00, 'perecible', 'Mercado Mayorista N1, Lima', 300.00, CURRENT_DATE + 1, FALSE, 'en_revision'),       -- para EP20 (supervisar cargas en revision)
    ('C-2026-004', 1, 1, 2,    600.00, 'perecible', 'Mercado Mayorista N1, Lima', 900.00, CURRENT_DATE + 2, FALSE, 'confirmada_en_lote'),-- ya asignada al lote 2 (confirmado)
    ('C-2026-005', 2, 4, 3,    700.00, 'perecible', 'Mercado Mayorista N1, Lima', 1000.00,CURRENT_DATE - 1, FALSE, 'en_transito'),       -- viajando en el lote 3
    ('C-2026-006', 1, 1, 5,    400.00, 'perecible', 'Mercado Mayorista N1, Lima', 600.00, CURRENT_DATE - 3, FALSE, 'entregada'),         -- lote 5 (entregado) - productor 1
    ('C-2026-007', 2, 2, 5,    350.00, 'perecible', 'Mercado Mayorista N1, Lima', 550.00, CURRENT_DATE - 3, FALSE, 'entregada'),         -- lote 5 - productor 2
    ('C-2026-008', 3, 3, 5,    450.00, 'perecible', 'Mercado Mayorista N1, Lima', 620.00, CURRENT_DATE - 3, FALSE, 'entregada'),         -- lote 5 - productor 3
    ('C-2026-009', 4, 4, 5,    380.00, 'perecible', 'Mercado Mayorista N1, Lima', 580.00, CURRENT_DATE - 3, FALSE, 'entregada'),         -- lote 5 - productor 4
    ('C-2026-010', 5, 5, 5,    420.00, 'perecible', 'Mercado Mayorista N1, Lima', 610.00, CURRENT_DATE - 3, FALSE, 'entregada');         -- lote 5 - productor 5


/* ------------------------------------------------------------
   6. SOLICITUD_CHAT — 5 filas
   id_productor 1..5 | id_transportista 6..9 | id_lote 1..5
   ------------------------------------------------------------ */
INSERT INTO solicitud_chat (id_productor, id_transportista, id_lote, id_administrador, estado_solicitud) VALUES
    (1, 6, 2, 11,   'aprobado'),   -- id_solicitud 1: chat activo, sirve para EP13 (enviar mensaje)
    (2, 7, 3, NULL, 'pendiente'),  -- id_solicitud 2: sirve para EP12 (aprobar/rechazar)
    (1, 9, 5, 11,   'aprobado'),   -- id_solicitud 3: chat del lote ya entregado
    (4, 8, 4, NULL, 'pendiente'),  -- id_solicitud 4
    (3, 6, 1, 12,   'rechazado');  -- id_solicitud 5: ejemplo de solicitud rechazada


/* ------------------------------------------------------------
   7. MENSAJE_CHAT — 5 filas (sobre las solicitudes ya aprobadas: 1 y 3)
   ------------------------------------------------------------ */
INSERT INTO mensaje_chat (id_solicitud, id_usuario_emisor, contenido, fecha_envio) VALUES
    (1, 1, 'Hola, ¿a que hora recogen la carga?',        CURRENT_TIMESTAMP - INTERVAL '3 hours'),
    (1, 6, 'Buenas, mañana a las 7am estamos por ahi',    CURRENT_TIMESTAMP - INTERVAL '2 hours 50 minutes'),
    (1, 1, 'Perfecto, los espero en la chacra',           CURRENT_TIMESTAMP - INTERVAL '2 hours 40 minutes'),
    (3, 9, 'Carga entregada sin problemas en el mercado', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (3, 1, 'Gracias, todo llego en buen estado',          CURRENT_TIMESTAMP - INTERVAL '23 hours');


/* ------------------------------------------------------------
   8. CALIFICACION — 5 filas, las 5 sobre el lote entregado (id_lote 5),
   una por cada productor que participo en ese lote consolidado.
   ------------------------------------------------------------ */
INSERT INTO calificacion (id_lote, id_productor, estrellas, comentario, recomienda) VALUES
    (5, 1, 5, 'Excelente servicio, muy puntual',        TRUE),
    (5, 2, 4, 'Buen trato, todo llego bien',             TRUE),
    (5, 3, 3, 'Normal, llego con un poco de retraso',    FALSE),
    (5, 4, 5, 'Muy recomendado, sin ningun problema',    TRUE),
    (5, 5, 2, 'Llego con parte de la carga dañada',      FALSE);


/* ------------------------------------------------------------
   9. PRECIO_MERCADO — 5 filas, una por cultivo, precio de hoy
   ------------------------------------------------------------ */
INSERT INTO precio_mercado (id_cultivo, precio_por_kg, fecha_precio) VALUES
    (1, 1.80, CURRENT_DATE),  -- Papa blanca
    (2, 2.10, CURRENT_DATE),  -- Cebolla roja
    (3, 1.50, CURRENT_DATE),  -- Zanahoria
    (4, 1.20, CURRENT_DATE),  -- Maiz
    (5, 1.90, CURRENT_DATE);  -- Camote


/* ------------------------------------------------------------
   10. NOTIFICACION — 5 filas, variadas entre productor/transportista/admin
   ------------------------------------------------------------ */
INSERT INTO notificacion (id_usuario, titulo, mensaje, leida) VALUES
    (1,  'Camion consolidado',            'Tu carga fue asignada al lote L-2026-002.',       FALSE),
    (1,  'Nueva calificacion recibida',   'Recibiste una calificacion de 5 estrellas.',      TRUE),
    (6,  'Solicitud de chat aprobada',    'El administrador aprobo tu chat con el productor.', FALSE),
    (11, 'Carga en revision',             'Hay una carga nueva esperando revision (C-2026-003).', FALSE),
    (2,  'Precio de mercado actualizado', 'El precio de la cebolla roja se actualizo hoy.',  TRUE);


/* ------------------------------------------------------------
   11. GASTO_VIAJE — 5 filas, sobre el lote entregado (5) y el que
   esta en transito (3), para poder probar el total por lote.
   ------------------------------------------------------------ */
INSERT INTO gasto_viaje (id_lote, concepto, monto) VALUES
    (5, 'Peaje Chilca',     18.50),
    (5, 'Combustible',      220.00),
    (5, 'Peaje Pucusana',   12.00),
    (3, 'Peaje Izcuchaca',  15.00),
    (3, 'Combustible',      180.00);


-- Verificacion rapida: cuantas filas quedaron por tabla
SELECT 'rol' AS tabla, COUNT(*) FROM rol
UNION ALL SELECT 'cultivo', COUNT(*) FROM cultivo
UNION ALL SELECT 'usuario', COUNT(*) FROM usuario
UNION ALL SELECT 'lote', COUNT(*) FROM lote
UNION ALL SELECT 'carga', COUNT(*) FROM carga
UNION ALL SELECT 'solicitud_chat', COUNT(*) FROM solicitud_chat
UNION ALL SELECT 'mensaje_chat', COUNT(*) FROM mensaje_chat
UNION ALL SELECT 'calificacion', COUNT(*) FROM calificacion
UNION ALL SELECT 'precio_mercado', COUNT(*) FROM precio_mercado
UNION ALL SELECT 'notificacion', COUNT(*) FROM notificacion
UNION ALL SELECT 'gasto_viaje', COUNT(*) FROM gasto_viaje;
