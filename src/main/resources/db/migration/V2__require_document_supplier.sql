-- The Documento domain and service require every document to belong to a supplier.
ALTER TABLE documento
    ALTER COLUMN proveedor_id SET NOT NULL;
