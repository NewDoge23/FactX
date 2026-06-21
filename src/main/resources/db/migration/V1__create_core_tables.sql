-- FactX v0.0.1 core schema baseline.
-- These tables are intentionally limited to the future v1 domain.
-- No users, roles, OCR, scanning, sync or AI tables belong in v1.

CREATE TABLE IF NOT EXISTS proveedor (
    id BIGSERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    cuit TEXT,
    notas TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS documento (
    id BIGSERIAL PRIMARY KEY,
    proveedor_id BIGINT REFERENCES proveedor(id),
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

CREATE TABLE IF NOT EXISTS adjunto (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL REFERENCES documento(id) ON DELETE CASCADE,
    nombre_archivo TEXT NOT NULL,
    ruta_local TEXT NOT NULL,
    tipo_contenido TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS pago (
    id BIGSERIAL PRIMARY KEY,
    documento_id BIGINT NOT NULL REFERENCES documento(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    monto NUMERIC(14,2) NOT NULL,
    metodo TEXT,
    referencia TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_documento_proveedor ON documento(proveedor_id);
CREATE INDEX IF NOT EXISTS idx_documento_fecha_emision ON documento(fecha_emision);
CREATE INDEX IF NOT EXISTS idx_adjunto_documento ON adjunto(documento_id);
CREATE INDEX IF NOT EXISTS idx_pago_documento ON pago(documento_id);
