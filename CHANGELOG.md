# Changelog

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
