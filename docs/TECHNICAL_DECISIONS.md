# Technical Decisions

## Java 21

FactX uses Java 21 because it is a modern LTS version and fits the portfolio goal of showing current Java skills.

## Maven

FactX resets to Maven for a simple, widely recognized Java build structure.

## JavaFX

FactX is a desktop app. JavaFX keeps the UI local, inspectable and appropriate for a portfolio project focused on Java.

`v0.0.8` keeps the shell in FXML because the project already uses FXML and its layout is declarative. `MainShellController` owns only local navigation and changes a single central content area from `NavigationDestination`; it does not access services, repositories or database configuration. The Proveedores and Documentos entries remain placeholders until their scheduled milestones, and starting the shell does not create a database pool or run migrations.

## PostgreSQL

PostgreSQL is used for realistic relational persistence during development and demos.

## Flyway

Flyway records schema evolution explicitly and keeps database changes reviewable.

FactX does not run `Flyway.repair()` automatically. Migration failures should stay visible and fail clearly.

## HikariCP

HikariCP provides a small, standard JDBC connection pool.

The initial pool name is `FactXPool`. The JavaFX shell does not create a pool during startup yet.

## Jdbi

Jdbi keeps persistence explicit without adding a heavy ORM layer.

Jdbi is created from a `DataSource` when persistence code needs it.

`v0.0.5` adds base repositories for suppliers and documents. Repository SQL stays explicit and small, with no Hibernate/JPA, no Spring and no UI coupling.

`v0.0.6` adds small concrete services over those repositories. The services keep the use-case rules outside JavaFX: supplier IDs must be positive, documents need an existing supplier, due dates cannot precede issue dates, currency codes use three uppercase letters and totals preserve the database's two-decimal precision. Unit tests use local repository stubs, so they remain independent of PostgreSQL without adding a mock framework or a generic repository abstraction.

## Synthetic Demo Data Loader

`v0.0.7` adds `DemoDataLoader` as an explicit development tool. It uses the existing supplier and document services after the normal database bootstrap; it is not triggered by JavaFX startup or unit tests.

The fixed dataset uses clearly fictional `FactX Demo` supplier names, the placeholder CUIT `00-00000000-0` and per-document synthetic note markers. On a repeated sequential run, the loader treats an exact supplier name and marker as already present and skips it. This application-level check is intentionally limited to the static demo dataset; it does not introduce a generic fixture framework, a database constraint or any new business rule.

## Database Bootstrap Check

`v0.0.3` adds a command-line technical check for the development PostgreSQL database. It creates the configured pool, runs Flyway explicitly, validates `SELECT 1` and verifies the four current core tables.

This check is intentionally separate from the JavaFX startup. It is a development validation tool, not a user-facing feature.

## Repository Check

`v0.0.5` adds a command-line technical check for the base repositories. It inserts synthetic supplier and document rows, reads them back and cleans them up.

This check is explicit and separate from `mvn clean test`, so unit tests do not require PostgreSQL.

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

FactX v1 manages received supplier documents. A possible post-v1 FijaStock integration concerns imported external sales and eventual issued billing, so it must not reuse the current `Proveedor`, `Documento`, `EstadoDocumento` or `TipoDocumento` concepts for customers, sales, billing states or requested receipt types.

The future boundary is transport-agnostic: a versioned sale snapshot can reach a `SaleImportService` through an adapter, while issuance remains behind a separate billing-provider abstraction. There is no HTTP, direct SQLite access, shared database or ARCA implementation today.

Future imports must retain complete customer and line-item snapshots, so FactX never needs to query FijaStock to reconstruct a sale. Their external identity is conceptually `(source_system, external_sale_id)` and must eventually be protected with a database `UNIQUE` constraint. Repeated equivalent data should be reported as already imported; incompatible repeated data must be a conflict rather than a silent overwrite. Further context is in [Future Integrations](FUTURE_INTEGRATIONS.md).
