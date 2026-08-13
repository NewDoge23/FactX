# FactX

FactX is a local-first JVM desktop portfolio project for internal control of received and issued commercial documents in small businesses.

Current version: `v0.0.11`.

Latest milestone: the desktop shell now uses Kotlin and Compose Desktop while the domain, services, repositories and tests remain Java.

This repository is a clean reset. The previous prototype is kept only as a local ignored backup in `_factx_legacy_local/` and must not be committed.

## Goal For FactX v1

FactX v1 is a portfolio-ready desktop application, not a commercial product yet. The goal is to show a simple, defensible JVM application for managing the operational side of commercial documents.

FactX v1 should eventually include:

- suppliers, received documents, attachments, states and payments;
- customers, issued documents, attachments, states and collections;
- filters;
- CSV export;
- a minimal dashboard;
- simple payments if they stay small and useful.

FactX v1 intentionally excludes login and roles, OCR, scanning, sync, AI, multi-branch or multi-tenant workflows, final installer packaging and SaaS behavior.

Docker is allowed only as a development and portfolio-demo helper for PostgreSQL. It is not a final product dependency.

## Stack

- Java 21 for the existing core domain, services, repositories and tests;
- Kotlin 2.4.10 and Compose Desktop 1.11.1 for the desktop shell;
- Gradle Kotlin DSL with the checked-in Gradle 8.14.4 wrapper;
- PostgreSQL, Flyway, HikariCP and Jdbi;
- PDFBox;
- SLF4J + Logback;
- JUnit 5.

There is no Spring Boot, Hibernate/JPA, OCR, scanner integration, AI, login, roles or sync in `v0.0.11`.

## Configuration

FactX reads database configuration from environment variables. Missing values use development defaults:

| Variable | Default |
| --- | --- |
| `FACTX_DB_URL` | `jdbc:postgresql://localhost:5432/factx` |
| `FACTX_DB_USER` | `factx` |
| `FACTX_DB_PASSWORD` | `factx` |
| `FACTX_DB_POOL_SIZE` | `5` |

The database wiring is present for repositories and explicit development tools. The Compose desktop shell displays local demo data and does not create database connections, run migrations or query PostgreSQL at startup.

## Run

On Windows, compile and run the unit suite without PostgreSQL:

```powershell
.\gradlew.bat clean test
```

On macOS or Linux, use:

```bash
./gradlew clean test
```

Start the Compose Desktop shell:

```powershell
.\gradlew.bat run
```

The navigation and dashboard are local, polished demo views. They are intentionally not CRUD screens and do not access the database.

Optional development database:

```bash
docker compose up -d
```

Docker is only for development and portfolio demos. It is not a dependency of the desktop shell.

Run the technical database check against the development PostgreSQL container:

```powershell
.\gradlew.bat databaseCheck
```

It validates PostgreSQL connectivity, runs Flyway migrations explicitly and confirms the core tables exist. It is a development check, not a user-facing feature.

Run the technical repository check:

```powershell
.\gradlew.bat repositoryCheck
```

It inserts synthetic supplier, received-document, customer and issued-document rows, reads them through the Jdbi repositories and removes them.

Load the explicit synthetic demo dataset:

```powershell
.\gradlew.bat demoDataLoader
```

The loader creates five fictional `FactX Demo` suppliers with six received documents, and four fictional customers with five issued documents. It is never invoked by the Compose startup or the unit suite; repeated runs are idempotent.

For a complete clean-clone runbook, see [Clean Clone Checklist](docs/CLEAN_CLONE_CHECKLIST.md).

If Docker Desktop is not running, `docker compose up -d` may fail with a message about `dockerDesktopLinuxEngine` or a missing Docker pipe. Open Docker Desktop and run the command again.

Docker may warn about orphan containers such as `factx-db-1` when an older compose service name exists locally. FactX does not remove those automatically. If you intend to clean them manually, run:

```bash
docker compose up -d --remove-orphans
```

## Status

`v0.0.11` is a visual and build migration only. It adds no business UI CRUD, ARCA or fiscal issuance, HTTP, sync or FijaStock runtime integration.
