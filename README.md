# FactX

FactX is being rebuilt as a clean Java desktop portfolio project for internal receipt and invoice control in small businesses.

Current version: `v0.0.2`.

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

No Spring Boot, Hibernate/JPA, OCR, scanner integration, AI, login, roles or sync are included in `v0.0.2`.

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

The app does not need Docker to open the current `v0.0.2` window.

## Status

`v0.0.2` contains the clean Maven base, a minimal JavaFX window, initial documentation, a development PostgreSQL compose file, a first Flyway migration for the future core tables and explicit database configuration classes.
