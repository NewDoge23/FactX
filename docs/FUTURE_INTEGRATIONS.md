# Future Integrations

## Purpose And Timing

This document records a possible integration between FijaStock and FactX after FactX v1. It is architectural context only. It does not add a runtime integration, database schema, API endpoint, network dependency or fiscal-provider implementation to the current product.

FactX v1 remains a local internal-control application for supplier documents. It must not be expanded into an ERP, a sales system or fiscal software because of this future possibility.

## Intended Relationship

FijaStock is a separate Java 17 and SQLite application for a hardware store. A future integration may allow it to send a completed sale to FactX through a versioned, transport-agnostic contract:

```text
FijaStock
  -> versioned sale DTO / JSON
  -> transport adapter
  -> SaleImportService
  -> FactX sale aggregate

FactX sale aggregate
  -> billing / issuance abstraction
  -> ARCA or another provider
```

The transport adapter could eventually be HTTP, a file exchange or another mechanism. That choice is intentionally deferred. FactX must not share a database with FijaStock or query its SQLite tables directly.

## Future Import Contract

A future `SaleImportRequestV1` should be versioned and contain a complete historical snapshot. At minimum it needs:

- source identity: `source_system` (initially `FIJASTOCK`), `external_sale_id` as UUID and contract version;
- customer snapshot: optional external customer ID, name, legal name, CUIT and requested receipt (`A`, `B` or `NONE`);
- sale data: date/time, currency (initially `ARS`), subtotal, discount, total, payment method and billing status;
- line-item snapshots: line position, external product ID, SKU, description, quantity, unit, historic unit price, line discount and line total.

FactX must retain enough data to reconstruct the sale without reading FijaStock again. The exact JSON shape, serialization library and persistence mapping should be designed only when implementation begins.

## Idempotency And Conflict Handling

The external identity is conceptually the pair:

```text
(source_system, external_sale_id)
```

When the future aggregate is persisted, the database must enforce that identity with a `UNIQUE` constraint. A future `SaleImportResult` should distinguish:

- `IMPORTED` for a new compatible sale;
- `ALREADY_IMPORTED` when the same identity and equivalent historical data arrive again;
- `CONFLICT` when the same identity arrives with incompatible data.

An import must never silently overwrite a historical sale.

## Separate Concepts

The current FactX domain represents received supplier documents. The future integration represents external sales and possible issued fiscal documents. These concepts must remain separate:

- `Proveedor` is not a customer.
- `Documento` is not an imported external sale.
- `EstadoDocumento` is not a billing status.
- `TipoDocumento` is not the requested invoice type `A`, `B` or `NONE`.

A future external sale should therefore be its own aggregate with its own line items and snapshots, rather than extending the current `Documento` model.

## Future Billing Boundary

The future sale lifecycle is separate from `EstadoDocumento`:

```text
PENDING -> PROCESSING -> ISSUED / ERROR
```

For a requested receipt of `NONE`, `NOT_REQUIRED` is also needed. When issuance is implemented, FactX should retain the receipt type and number, CAE and expiry, provider/ARCA response data, generated PDF or document, error message and data needed for safe retries.

This is not an ARCA design. It only preserves the boundary so that a future billing provider can be introduced independently of the FijaStock import transport.

## Explicit Non-Goals Today

The current repository intentionally contains none of the following:

- FijaStock communication or runtime dependency;
- HTTP controllers, endpoints or network infrastructure;
- ARCA integration or fiscal issuance;
- external sale, customer or line-item tables;
- shared databases or direct SQLite access;
- import idempotency implementation or migration.

Revisit this document when FactX v1 is complete and a real integration milestone is approved.
