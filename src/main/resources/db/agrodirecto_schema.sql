/* ============================================================
   AgroDirecto - Modelo de datos reducido (11 tablas)
   Motor: SQL Server

   Cambios respecto a la version de 17 tablas:
   - productor, transportista, administrador  -> columnas de usuario
   - suscripcion                              -> usuario.premium_hasta
   - viaje                                    -> columnas de lote
   - transaccion                              -> columnas de lote
   Se mantiene la tabla rol.
   ============================================================ */

IF DB_ID('agrodirecto') IS NOT NULL
BEGIN
    ALTER DATABASE agrodirecto SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE agrodirecto;
END
GO

CREATE DATABASE agrodirecto;
GO

USE agrodirecto;
GO


/* ------------------------------------------------------------
   1. ROL
   ------------------------------------------------------------ */
CREATE TABLE rol (
    id_rol      INT IDENTITY(1,1) PRIMARY KEY,
    nombre_rol  VARCHAR(20) NOT NULL UNIQUE
        CHECK (nombre_rol IN ('productor', 'transportista', 'administrador'))
);
GO

-- Orden importante: 1 = productor, 2 = transportista, 3 = administrador
INSERT INTO rol (nombre_rol) VALUES ('productor'), ('transportista'), ('administrador');
GO


/* ------------------------------------------------------------
   2. USUARIO
   Absorbe: productor, transportista, administrador y suscripcion.
   Las columnas propias de cada rol son NULL para los demas roles.
   ------------------------------------------------------------ */
CREATE TABLE usuario (
    id_usuario          INT IDENTITY(1,1) PRIMARY KEY,
    nombre_completo     VARCHAR(120) NOT NULL,
    correo              VARCHAR(150) NOT NULL UNIQUE,
    contrasena          VARCHAR(255) NOT NULL,
    id_rol              INT NOT NULL,
    estado_cuenta       VARCHAR(20) NOT NULL DEFAULT 'pendiente'
        CHECK (estado_cuenta IN ('pendiente', 'verificado', 'bloqueado')),
    fecha_registro      DATETIME NOT NULL DEFAULT GETDATE(),

    -- Datos de productor (HU-01)
    dni                 CHAR(8) NULL,
    distrito            VARCHAR(100) NULL,

    -- Datos de transportista (HU-01)
    ruc                 CHAR(11) NULL,
    placa               VARCHAR(10) NULL,
    capacidad_toneladas DECIMAL(6,2) NULL,
    licencia            VARCHAR(20) NULL,

    -- Datos de administrador (HU-16)
    ultimo_acceso       DATETIME NULL,

    -- Plan premium (HU-21): es premium mientras premium_hasta > GETDATE()
    premium_hasta       DATETIME NULL,

    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol),

    -- Cada rol solo puede llenar sus propias columnas
    CONSTRAINT ck_usuario_datos_por_rol CHECK (
        (id_rol = 1 AND dni IS NOT NULL AND distrito IS NOT NULL
                    AND ruc IS NULL AND placa IS NULL AND capacidad_toneladas IS NULL)
     OR (id_rol = 2 AND ruc IS NOT NULL AND placa IS NOT NULL AND capacidad_toneladas IS NOT NULL
                    AND dni IS NULL AND distrito IS NULL)
     OR (id_rol = 3 AND dni IS NULL AND distrito IS NULL
                    AND ruc IS NULL AND placa IS NULL AND capacidad_toneladas IS NULL)
    )
);
GO

-- UNIQUE normal en SQL Server solo admite un NULL; se usan indices filtrados
CREATE UNIQUE INDEX uq_usuario_dni   ON usuario(dni)   WHERE dni   IS NOT NULL;
CREATE UNIQUE INDEX uq_usuario_ruc   ON usuario(ruc)   WHERE ruc   IS NOT NULL;
CREATE UNIQUE INDEX uq_usuario_placa ON usuario(placa) WHERE placa IS NOT NULL;
GO


/* ------------------------------------------------------------
   3. CULTIVO
   ------------------------------------------------------------ */
CREATE TABLE cultivo (
    id_cultivo      INT IDENTITY(1,1) PRIMARY KEY,
    nombre_cultivo  VARCHAR(60) NOT NULL UNIQUE
);
GO

INSERT INTO cultivo (nombre_cultivo)
VALUES ('Papa blanca'), ('Cebolla roja'), ('Zanahoria'), ('Maiz');
GO


/* ------------------------------------------------------------
   4. LOTE
   Absorbe: viaje y transaccion (ambas eran relaciones 1:1 con lote).
   Los datos de viaje y de transaccion son NULL hasta que el lote
   se confirma / se entrega.
   NOTA: id_transportista queda en NO ACTION para evitar rutas de
   borrado en cascada multiples (usuario -> carga -> lote). Los
   usuarios se bloquean (estado_cuenta), no se eliminan.
   ------------------------------------------------------------ */
CREATE TABLE lote (
    id_lote             INT IDENTITY(1,1) PRIMARY KEY,
    codigo_lote         VARCHAR(20) NOT NULL UNIQUE,
    id_transportista    INT NULL,
    estado_lote         VARCHAR(20) NOT NULL DEFAULT 'formandose'
        CHECK (estado_lote IN ('formandose', 'confirmado', 'en_transito',
                               'con_retraso', 'entregado', 'cancelado')),
    fecha_creacion      DATETIME NOT NULL DEFAULT GETDATE(),

    -- Datos del viaje (antes tabla viaje)
    origen              VARCHAR(100) NULL,
    destino             VARCHAR(100) NULL,
    fecha_salida        DATETIME NULL,
    fecha_llegada       DATETIME NULL,
    flete_total         DECIMAL(10,2) NULL,

    -- Datos de la transaccion (antes tabla transaccion)
    porcentaje_comision DECIMAL(4,2) NULL,
    monto_comision      DECIMAL(10,2) NULL,
    monto_neto          DECIMAL(10,2) NULL,
    estado_pago         VARCHAR(20) NULL
        CHECK (estado_pago IN ('pendiente', 'pagado')),
    fecha_transaccion   DATETIME NULL,

    CONSTRAINT ck_lote_fechas CHECK (fecha_llegada IS NULL OR fecha_salida IS NULL
                                     OR fecha_llegada >= fecha_salida),
    CONSTRAINT fk_lote_transportista FOREIGN KEY (id_transportista)
        REFERENCES usuario(id_usuario)
);
GO


/* ------------------------------------------------------------
   5. CARGA
   id_productor ahora referencia directamente a usuario.
   ------------------------------------------------------------ */
CREATE TABLE carga (
    id_carga            INT IDENTITY(1,1) PRIMARY KEY,
    codigo_carga        VARCHAR(20) NOT NULL UNIQUE,
    id_productor        INT NOT NULL,
    id_cultivo          INT NOT NULL,
    id_lote             INT NULL,
    peso_kg             DECIMAL(10,2) NOT NULL CHECK (peso_kg > 0),
    tipo_carga          VARCHAR(20) NOT NULL DEFAULT 'perecible'
        CHECK (tipo_carga IN ('perecible', 'seco')),
    direccion_destino   VARCHAR(150) NOT NULL,
    tarifa_propuesta    DECIMAL(10,2) NOT NULL CHECK (tarifa_propuesta > 0),
    fecha_recojo        DATE NOT NULL,
    es_urgente          BIT NOT NULL DEFAULT 0,
    estado_carga        VARCHAR(20) NOT NULL DEFAULT 'publicada'
        CHECK (estado_carga IN ('publicada', 'en_revision', 'confirmada_en_lote',
                                'en_transito', 'entregada', 'cancelada')),
    fecha_publicacion   DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_carga_productor FOREIGN KEY (id_productor)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_carga_cultivo FOREIGN KEY (id_cultivo)
        REFERENCES cultivo(id_cultivo) ON DELETE NO ACTION,
    CONSTRAINT fk_carga_lote FOREIGN KEY (id_lote)
        REFERENCES lote(id_lote) ON DELETE SET NULL
);
GO


/* ------------------------------------------------------------
   6. SOLICITUD_CHAT (HU-06)
   Productor, transportista y administrador referencian a usuario.
   ------------------------------------------------------------ */
CREATE TABLE solicitud_chat (
    id_solicitud        INT IDENTITY(1,1) PRIMARY KEY,
    id_productor        INT NOT NULL,
    id_transportista    INT NOT NULL,
    id_lote             INT NOT NULL,
    id_administrador    INT NULL,          -- quien aprobo/rechazo el chat
    estado_solicitud    VARCHAR(20) NOT NULL DEFAULT 'pendiente'
        CHECK (estado_solicitud IN ('pendiente', 'aprobado', 'rechazado')),
    fecha_solicitud     DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_solicitud UNIQUE (id_productor, id_transportista, id_lote),
    -- NO ACTION hacia usuario: SQL Server no permite multiples rutas en cascada
    CONSTRAINT fk_solicitud_productor FOREIGN KEY (id_productor)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_solicitud_transportista FOREIGN KEY (id_transportista)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_solicitud_administrador FOREIGN KEY (id_administrador)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_solicitud_lote FOREIGN KEY (id_lote)
        REFERENCES lote(id_lote) ON DELETE CASCADE
);
GO


/* ------------------------------------------------------------
   7. MENSAJE_CHAT (HU-06 / HU-18)
   ------------------------------------------------------------ */
CREATE TABLE mensaje_chat (
    id_mensaje          INT IDENTITY(1,1) PRIMARY KEY,
    id_solicitud        INT NOT NULL,
    id_usuario_emisor   INT NOT NULL,
    contenido           VARCHAR(500) NOT NULL,
    fecha_envio         DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_mensaje_solicitud FOREIGN KEY (id_solicitud)
        REFERENCES solicitud_chat(id_solicitud) ON DELETE CASCADE,
    CONSTRAINT fk_mensaje_usuario FOREIGN KEY (id_usuario_emisor)
        REFERENCES usuario(id_usuario)
);
GO


/* ------------------------------------------------------------
   8. CALIFICACION (HU-04)
   Antes apuntaba a viaje; ahora apunta a lote (el viaje es el lote).
   ------------------------------------------------------------ */
CREATE TABLE calificacion (
    id_calificacion     INT IDENTITY(1,1) PRIMARY KEY,
    id_lote             INT NOT NULL,
    id_productor        INT NOT NULL,
    estrellas           TINYINT NOT NULL CHECK (estrellas BETWEEN 1 AND 5),
    comentario          VARCHAR(500) NULL,
    recomienda          BIT NULL,
    fecha_calificacion  DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_calificacion UNIQUE (id_lote, id_productor),
    CONSTRAINT fk_calificacion_lote FOREIGN KEY (id_lote)
        REFERENCES lote(id_lote) ON DELETE CASCADE,
    CONSTRAINT fk_calificacion_productor FOREIGN KEY (id_productor)
        REFERENCES usuario(id_usuario)
);
GO


/* ------------------------------------------------------------
   9. PRECIO_MERCADO (HU-07)
   ------------------------------------------------------------ */
CREATE TABLE precio_mercado (
    id_precio       INT IDENTITY(1,1) PRIMARY KEY,
    id_cultivo      INT NOT NULL,
    precio_por_kg   DECIMAL(6,2) NOT NULL,
    fecha_precio    DATE NOT NULL,
    CONSTRAINT uq_precio UNIQUE (id_cultivo, fecha_precio),
    CONSTRAINT fk_precio_cultivo FOREIGN KEY (id_cultivo)
        REFERENCES cultivo(id_cultivo) ON DELETE CASCADE
);
GO


/* ------------------------------------------------------------
   10. NOTIFICACION (HU-08)
   ------------------------------------------------------------ */
CREATE TABLE notificacion (
    id_notificacion INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario      INT NOT NULL,
    titulo          VARCHAR(150) NOT NULL,
    mensaje         VARCHAR(500) NOT NULL,
    leida           BIT NOT NULL DEFAULT 0,
    fecha_creacion  DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);
GO


/* ------------------------------------------------------------
   11. GASTO_VIAJE (HU-20)
   Antes apuntaba a viaje; ahora apunta a lote.
   ------------------------------------------------------------ */
CREATE TABLE gasto_viaje (
    id_gasto        INT IDENTITY(1,1) PRIMARY KEY,
    id_lote         INT NOT NULL,
    concepto        VARCHAR(100) NOT NULL,
    monto           DECIMAL(10,2) NOT NULL CHECK (monto > 0),
    fecha_hora      DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_gasto_lote FOREIGN KEY (id_lote)
        REFERENCES lote(id_lote) ON DELETE CASCADE
);
GO


-- Verificacion: deben aparecer 11 tablas
SELECT COUNT(*) AS total_tablas FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
GO
