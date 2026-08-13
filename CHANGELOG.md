# Changelog

## v0.0.11 - Compose Desktop migration

- Replaced the JavaFX/Maven desktop shell with Kotlin 2.4.10, Compose Desktop 1.11.1 and Gradle Kotlin DSL.
- Added the local-only FactX visual shell: sidebar navigation, custom X logo, dashboard demo and milestone placeholders.
- Preserved the Java 21 domain, services, repositories, Flyway migrations and 35 unit tests.
- Added Gradle tasks for the explicit database, repository and demo-data checks.

## v0.0.10 - Received and issued document foundations

- Split the commercial-document domain into explicit received and issued models and settlement states.
- Added customer, issued-document, collection and issued-attachment persistence foundations.
- Preserved existing received-document data through a Flyway rename migration.

## v0.0.9 - Pre-v0.1 project review

- Audited the technical base, documentation, tests, repository hygiene and milestone scope before starting supplier management.
- Added a Flyway migration that makes the document-to-supplier relationship mandatory, matching the domain and service rules.
- Removed the unused empty `util` package.

## v0.0.8 - JavaFX application shell

- Replaced the static JavaFX view with a header, local navigation and one central content area.
- Added explicit placeholders for supplier and document screens without business CRUD or persistence startup.
- Added pure unit coverage for the navigation destinations.

## v0.0.7 - Datos demo sintéticos y clean clone

- Added an explicit, idempotent synthetic demo-data loader for development and demos.
- Added unit coverage for the demo dataset composition and duplicate prevention.
- Added a practical clean-clone checklist and documented the loader workflow.

## v0.0.6 - Servicios y validación mínimos

- Added small supplier and document services over the existing repositories.
- Added validation for positive identifiers, supplier existence, document dates, currency shape and monetary precision.
- Added unit coverage for services and validation without requiring PostgreSQL.
- Documented a post-v1, decoupled future boundary for FijaStock sales imports and billing.

## v0.0.5 - Persistencia base sin UI

- Added minimal supplier and document domain models.
- Added base Jdbi repositories.
- Added tests for persistence foundation.

## v0.0.4 - Entorno dev y roadmap estratégico

- Normalized/documented timezone handling for PostgreSQL development checks.
- Added Docker troubleshooting notes.
- Documented orphan container guidance.
- Replaced roadmap with a more strategic, granular plan toward the first public milestone.

## v0.0.3 - Bootstrap técnico de base de datos

- Added a technical database bootstrap that runs Flyway, validates connectivity and checks core tables.
- Added a Maven-executable `DatabaseCheck` utility for the development PostgreSQL database.
- Added unit tests for core table verification logic without requiring PostgreSQL.
- Documented the development database check flow.

## v0.0.2 - Configuración inicial de base de datos

- Added environment-based application configuration.
- Added HikariCP configuration with the `FactXPool` pool name.
- Added a simple Jdbi factory path from a `DataSource`.
- Added explicit Flyway migration runner without automatic repair.
- Added unit tests for configuration defaults and pool settings.

## v0.0.1 - Repo sanitizado y base Maven inicial

- Reset clean repository history for FactX.
- Recreated the project as a Maven Java 21 application.
- Added a minimal JavaFX shell.
- Added initial product, roadmap and technical decision docs.
- Added development-only PostgreSQL Docker Compose.
- Added initial Flyway migration for future core tables.
