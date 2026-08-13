# Clean Clone Checklist

Use this checklist to validate the technical base from a clean FactX checkout. It uses only synthetic development data.

## Requirements

- Git;
- Java 21;
- Docker Desktop with Docker Compose for the development PostgreSQL database.

## From A Fresh Clone

1. Clone the repository and enter it:

   ```bash
   git clone https://github.com/NewDoge23/FactX.git
   cd FactX
   ```

2. Confirm Java 21 is available. Gradle is provided by the checked-in wrapper:

   ```bash
   java -version
   ./gradlew --version
   ```

3. Start the development database:

   ```bash
   docker compose up -d
   ```

4. Compile and run the unit suite. This command must not require PostgreSQL:

   ```bash
   ./gradlew clean test
   ```

5. Run the explicit PostgreSQL bootstrap check:

   ```bash
   ./gradlew databaseCheck
   ```

6. Run the explicit repository check. It inserts and removes its own synthetic rows:

   ```bash
   ./gradlew repositoryCheck
   ```

7. Load the fixed synthetic demo dataset:

   ```bash
   ./gradlew demoDataLoader
   ```

   The first run loads synthetic received and issued fixtures. Run the same command a second time: it should report zero newly created received or issued rows.

8. Start the Compose Desktop shell if a visual environment is available:

   ```bash
   ./gradlew run
   ```

The current shell does not load database data automatically. The loader exists so later business-screen milestones have a safe, predictable development dataset.

## Development Database Defaults

FactX reads the following environment variables. The Docker Compose configuration uses the same development-only defaults.

| Variable | Default |
| --- | --- |
| `FACTX_DB_URL` | `jdbc:postgresql://localhost:5432/factx` |
| `FACTX_DB_USER` | `factx` |
| `FACTX_DB_PASSWORD` | `factx` |
| `FACTX_DB_POOL_SIZE` | `5` |

## Troubleshooting

- **Java version:** `java -version` must report Java 21. Install or select Java 21 before running the build.
- **PostgreSQL timezone:** `DatabaseCheck`, `RepositoryCheck` and `DemoDataLoader` configure `America/Argentina/Buenos_Aires` themselves. For Java commands run manually outside those tools, use a PostgreSQL-compatible timezone such as `JAVA_TOOL_OPTIONS=-Duser.timezone=America/Argentina/Buenos_Aires`.
- **Docker Desktop unavailable:** a message mentioning `dockerDesktopLinuxEngine` or a missing Docker pipe means Docker Desktop is not running. Start it and retry `docker compose up -d`.
- **Orphan containers:** if Docker reports an older `factx-db-1` service, review it first and then run `docker compose up -d --remove-orphans` if that cleanup is intended.
- **Port already occupied:** inspect the local process or Docker container using PostgreSQL's configured port before changing the compose or `FACTX_DB_URL` configuration.
- **Credentials or custom database:** set the `FACTX_DB_*` variables consistently before running the explicit database tools.

No legacy-local files, private documents or real receipts are required for this flow.
