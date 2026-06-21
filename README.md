# FactX

FactX is being rebuilt as a clean Java desktop portfolio project for internal receipt and invoice control in small businesses.

Current version: `v0.0.5`.

This repository is a clean reset. The previous prototype is kept only as a local ignored backup in `_factx_legacy_local/` and must not be committed.

## Goal For FactX v1

FactX v1 is a portfolio-ready desktop application, not a commercial product yet. The goal is to show a simple, defensible Java application for managing the operational side of commercial documents.

FactX v1 should eventually include:

- suppliers;
- documents and receipts;
- attachments;
- document states;
- filters;
- CSV export;
- a minimal dashboard;
- simple payments if they stay small and useful.

FactX v1 intentionally excludes:

- login and roles;
- OCR;
- scanning;
- sync;
- AI;
- multi-branch or multi-tenant workflows;
- final installer packaging;
- SaaS behavior.

Docker is allowed only as a development and portfolio demo helper for PostgreSQL. It is not a final product dependency.

## Stack

- Java 21
- Maven
- JavaFX
- PostgreSQL
- Flyway
- HikariCP
- Jdbi
- PDFBox
- SLF4J + Logback
- JUnit 5

No Spring Boot, Hibernate/JPA, OCR, scanner integration, AI, login, roles or sync are included in `v0.0.5`.

## Configuration

FactX reads database configuration from environment variables. Missing values use development defaults:

| Variable | Default |
| --- | --- |
| `FACTX_DB_URL` | `jdbc:postgresql://localhost:5432/factx` |
| `FACTX_DB_USER` | `factx` |
| `FACTX_DB_PASSWORD` | `factx` |
| `FACTX_DB_POOL_SIZE` | `5` |

The database wiring is present for future work, but the current JavaFX shell does not open database connections on startup.

## Run

Compile and test:

```bash
mvn clean test
```

Run the minimal JavaFX shell:

```bash
mvn javafx:run
```

Optional development database:

```bash
docker compose up -d
```

Docker is only for development and portfolio demos. It is not a dependency of the final desktop product.

Run the technical database check against the development PostgreSQL container:

```bash
mvn exec:java -Dexec.mainClass=ar.com.gaston.factx.tools.DatabaseCheck
```

The database check validates PostgreSQL connectivity, runs Flyway migrations explicitly and confirms the core tables exist. It is a technical development check, not a user-facing feature. The check normalizes the Java timezone to `America/Argentina/Buenos_Aires`, which PostgreSQL accepts for the local development environment.

Run the technical repository check against the development PostgreSQL container:

```bash
mvn exec:java -Dexec.mainClass=ar.com.gaston.factx.tools.RepositoryCheck
```

The repository check inserts synthetic supplier and document rows, reads them back through the Jdbi repositories and deletes them. It is an explicit development check, not a user-facing feature.

If running Java commands manually outside the Maven check, use a PostgreSQL-compatible timezone:

```bash
JAVA_TOOL_OPTIONS=-Duser.timezone=America/Argentina/Buenos_Aires
```

If Docker Desktop is not running, `docker compose up -d` may fail with a message about `dockerDesktopLinuxEngine` or a missing Docker pipe. Open Docker Desktop and run the command again.

Docker may warn about orphan containers such as `factx-db-1` when an older compose service name exists locally. FactX does not remove those automatically. If you want to clean them manually, run:

```bash
docker compose up -d --remove-orphans
```

The app does not need Docker to open the current `v0.0.5` window.

## Status

`v0.0.5` contains the clean Maven base, a minimal JavaFX window, initial documentation, a development PostgreSQL compose file, a first Flyway migration for the future core tables, explicit database configuration classes, technical database checks, timezone hardening, a strategic roadmap and base supplier/document persistence repositories.
