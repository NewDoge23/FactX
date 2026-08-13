# Technical Decisions

## Java 21

FactX uses Java 21 because it is a modern LTS version and fits the portfolio goal of showing current Java skills.

## Gradle Kotlin DSL

`v0.0.11` replaces Maven with the checked-in Gradle 8.14.4 wrapper and Kotlin DSL. The wrapper makes the build reproducible without requiring a local Gradle installation, while retaining Java 21 compilation for the core.

## Kotlin And Compose Desktop

FactX is a desktop app. Kotlin 2.4.10 and Compose Desktop 1.11.1 provide a local, inspectable UI without changing the Java domain, services, repositories or test suite.

The v0.0.11 shell owns only local navigation and demo presentation. Its sidebar, custom X logo, dashboard and placeholders do not access services, repositories or database configuration; starting it does not create a database pool, run migrations or query PostgreSQL. The visual system uses the named `FactXColors` and `FactXTokens` primitives so the shell remains coherent without introducing a component framework.

## PostgreSQL

PostgreSQL is used for realistic relational persistence during development and demos.

## Flyway

Flyway records schema evolution explicitly and keeps database changes reviewable.

FactX does not run `Flyway.repair()` automatically. Migration failures should stay visible and fail clearly.

## HikariCP

HikariCP provides a small, standard JDBC connection pool.

The initial pool name is `FactXPool`. The Compose shell does not create a pool during startup.

## Jdbi

Jdbi keeps persistence explicit without adding a heavy ORM layer.

Jdbi is created from a `DataSource` when persistence code needs it.

`v0.0.10` keeps repository SQL explicit and small for `Proveedor`, `DocumentoRecibido`, `Cliente` and `DocumentoEmitido`, with no Hibernate/JPA, no Spring and no UI coupling.

Small concrete services keep use-case rules outside the UI: counterparty IDs must be positive, received documents need an existing supplier, issued documents need an existing customer, due dates cannot precede issue dates, currency codes use three uppercase letters and totals preserve the database's two-decimal precision. Unit tests use local repository stubs, so they remain independent of PostgreSQL without a mock framework or generic repository abstraction.

## Synthetic Demo Data Loader

`v0.0.7` adds `DemoDataLoader` as an explicit development tool. It uses the existing supplier and document services after the normal database bootstrap; it is not triggered by Compose startup or unit tests.

The fixed dataset uses clearly fictional `FactX Demo` supplier names, the placeholder CUIT `00-00000000-0` and per-document synthetic note markers. On a repeated sequential run, the loader treats an exact supplier name and marker as already present and skips it. This application-level check is intentionally limited to the static demo dataset; it does not introduce a generic fixture framework, a database constraint or any new business rule.

## Database Bootstrap Check

`v0.0.3` adds a command-line technical check for the development PostgreSQL database. It creates the configured pool, runs Flyway explicitly, validates `SELECT 1` and verifies the current core tables (eight after the v0.0.10 received/issued split).

This check is intentionally separate from the Compose startup. It is a development validation tool, not a user-facing feature.

## Repository Check

`v0.0.5` adds a command-line technical check for the base repositories. It inserts synthetic supplier and document rows, reads them back and cleans them up.

This check is explicit and separate from `./gradlew clean test`, so unit tests do not require PostgreSQL.

## PDFBox

PDFBox is included because FactX v1 will eventually manage PDF attachments. OCR and scanning remain out of scope.

## SLF4J + Logback

SLF4J and Logback provide conventional application logging without tying the project to a framework.

## JUnit 5

JUnit 5 is the default test foundation for Java code.

## Why Not Spring Boot

FactX v1 is a desktop app, not a web service. Spring Boot would add startup, configuration and architectural weight before the project needs it.

## Why Not Hibernate

The data model should stay simple and explicit. Jdbi is easier to explain for a small portfolio application.

## Why No Login In v1

FactX v1 is local and portfolio-oriented. Login and roles add security expectations that are not necessary for the first stable version.

## Why OCR, Scanning And Sync Are Out Of v1

OCR, scanner integration and sync are useful future features, but they add external dependencies and operational complexity. They should return only after the document, supplier and payment workflows are stable.

## Future External Sales And Billing Boundary

FactX v1 manages received supplier documents and manually recorded issued customer documents. A possible post-v1 FijaStock integration concerns imported external sales and future electronic billing, so it must not reuse `Proveedor`, `DocumentoRecibido`, `EstadoDocumentoRecibido`, `DocumentoEmitido`, `EstadoDocumentoEmitido` or `TipoDocumento` as sale aggregates, billing states or requested receipt types.

The future boundary is transport-agnostic: a versioned sale snapshot can reach a `SaleImportService` through an adapter, while issuance remains behind a separate billing-provider abstraction. There is no HTTP, direct SQLite access, shared database or ARCA implementation today.

Future imports must retain complete customer and line-item snapshots, so FactX never needs to query FijaStock to reconstruct a sale. Their external identity is conceptually `(source_system, external_sale_id)` and must eventually be protected with a database `UNIQUE` constraint. Repeated equivalent data should be reported as already imported; incompatible repeated data must be a conflict rather than a silent overwrite. Further context is in [Future Integrations](FUTURE_INTEGRATIONS.md).
