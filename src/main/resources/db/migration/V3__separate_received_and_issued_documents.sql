-- Preserve the original received-document records while making their direction explicit.
ALTER TABLE documento RENAME TO documento_recibido;
ALTER TABLE adjunto RENAME TO adjunto_recibido;
ALTER TABLE pago RENAME TO pago_realizado;

ALTER INDEX idx_documento_proveedor RENAME TO idx_documento_recibido_proveedor;
ALTER INDEX idx_documento_fecha_emision RENAME TO idx_documento_recibido_fecha_emision;
ALTER INDEX idx_adjunto_documento RENAME TO idx_adjunto_recibido_documento;
ALTER INDEX idx_pago_documento RENAME TO idx_pago_realizado_documento;

CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    razon_social TEXT,
    cuit TEXT,
    notas TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE documento_emitido (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id),
    tipo TEXT NOT NULL,
    numero TEXT,
    fecha_emision DATE,
    fecha_vencimiento DATE,
    moneda TEXT NOT NULL DEFAULT 'ARS',
    total NUMERIC(14,2) NOT NULL DEFAULT 0,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE',
    notas TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE adjunto_emitido (
    id BIGSERIAL PRIMARY KEY,
    documento_emitido_id BIGINT NOT NULL REFERENCES documento_emitido(id) ON DELETE CASCADE,
    nombre_archivo TEXT NOT NULL,
    ruta_local TEXT NOT NULL,
    tipo_contenido TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cobro (
    id BIGSERIAL PRIMARY KEY,
    documento_emitido_id BIGINT NOT NULL REFERENCES documento_emitido(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    monto NUMERIC(14,2) NOT NULL,
    metodo TEXT,
    referencia TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_emitido_cliente ON documento_emitido(cliente_id);
CREATE INDEX idx_documento_emitido_fecha_emision ON documento_emitido(fecha_emision);
CREATE INDEX idx_adjunto_emitido_documento ON adjunto_emitido(documento_emitido_id);
CREATE INDEX idx_cobro_documento_emitido ON cobro(documento_emitido_id);
