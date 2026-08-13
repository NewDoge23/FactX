# FactX Strategic Roadmap

## Product Direction

FactX is a local-first Java desktop portfolio application for small businesses that need a simple, private and understandable way to organize suppliers, invoices, receipts, local attachments, payment status and basic operational follow-up without becoming an ERP, accounting system or cloud platform.

FactX solves the everyday problem of scattered commercial documents: files in folders, receipts from chat apps, invoices from email and informal payment tracking. Its first goal is internal control, not legal accounting automation.

FactX is for small shops, service providers and owner-operated businesses that need clarity over documents, suppliers and pending payments.

FactX is not an ERP, not AFIP software, not legal bookkeeping, not SaaS and not a replacement for an accountant.

## `v1.0.0` Target Scope

FactX `v1.0.0` should include:

- supplier management;
- document/comprobante management;
- simple manual states;
- practical filters and search;
- local attachments;
- CSV export;
- minimal dashboard;
- simple payments if they stay small and explainable;
- synthetic demo data;
- clean README, screenshots and portfolio narrative.

It must be stable enough to demo from a clean clone, explain in an interview and show real Java architecture without overengineering.

## Explicitly Out Of Scope For `v1.0.0`

- ERP behavior;
- legal accounting;
- AFIP integration;
- SaaS;
- login and roles;
- OCR;
- scanner integration;
- sync;
- AI;
- multi-branch workflows;
- final installer packaging.

## Architecture Direction

Keep packages simple and boring:

- `ui`: JavaFX controllers, views and UI helpers.
- `domain`: records/classes/enums for suppliers, documents, states and payments.
- `repository`: Jdbi persistence code.
- `service`: small use-case services and transaction boundaries.
- `config`: app, database and Flyway configuration.
- `storage`: local attachment path handling.
- `export`: CSV export.
- `dashboard`: read models and summary calculations.
- `validation`: small reusable validation helpers.

Avoid Spring, Hibernate, complex layering, generic frameworks and premature abstractions.

## Cross-Cutting Requirements

- Local-first desktop behavior.
- No real receipts, invoices, screenshots of private documents or sensitive files in Git.
- Docker only for development and portfolio demo.
- Clean clone should be reproducible.
- `mvn clean test` must not require PostgreSQL.
- PostgreSQL/Flyway checks must stay explicit.
- Error messages should be clear and demo-friendly.
- Demo data must be synthetic.
- Documentation should be honest about scope and limits.
- Git tags may exist for every milestone.
- GitHub Releases should start at `v0.3.0`, when FactX has a demonstrable supplier-document workflow.

## Roadmap

### `v0.0.x` - Technical Base And Planning

- `v0.0.1`: clean repo reset, Maven, Java 21, minimal JavaFX shell, initial docs.
- `v0.0.2`: `AppConfig`, HikariCP, Jdbi and explicit Flyway migration runner.
- `v0.0.3`: technical database bootstrap, PostgreSQL dev validation, Flyway applies core tables.
- `v0.0.4`: timezone normalization, Docker troubleshooting, orphan container guidance, strategic roadmap update, stronger dev environment docs.
- `v0.0.5`: minimal Jdbi repositories for suppliers and documents, no UI.
- `v0.0.6`: minimal domain services and validation, no UI.
- `v0.0.7`: explicit idempotent synthetic demo data loader and clean clone checklist.
- `v0.0.8`: JavaFX structure prepared for business screens.
- `v0.0.9`: pre-`v0.1.0` review of docs, naming, tests, repo hygiene and scope.

Acceptance: technical base is reproducible, documented and free of business UI shortcuts.

### `v0.1.x` - Suppliers

- supplier domain model;
- supplier repository;
- supplier service;
- supplier list and form UI;
- basic validation;
- unit tests and repository checks;
- README notes for the first business feature.

Acceptance: create, edit, list and delete suppliers through the app with clear validation and no unrelated features.

### `v0.2.x` - Documents

- document domain model;
- document types;
- supplier-document relationship;
- document repository;
- document service;
- document list and form UI;
- validation for dates, totals and required fields;
- tests for persistence and service behavior.

Acceptance: create and manage documents linked to suppliers, with useful table output and simple errors.

### `v0.3.x` - Filters, States And First Public Milestone

- search and filters;
- manual document states;
- synthetic demo data;
- screenshots;
- README public polish;
- first formal GitHub Release;
- first LinkedIn publication.

Acceptance: a viewer can understand the project in GitHub and see a useful working app in a short demo.

### `v0.4.x` - Local Attachments

- local attachment path model;
- attach PDF or file path to documents;
- open local file action;
- validation for missing files;
- no OCR and no scanning.

Acceptance: local attachments work without committing files or pretending to manage cloud storage.

### `v0.5.x` - CSV Export

- export filtered document list to CSV;
- stable column order;
- UTF-8 output;
- tests for export formatting.

Acceptance: a user can export current document data locally and inspect it outside the app.

### `v0.6.x` - Minimal Dashboard

- document counts;
- supplier counts;
- pending document summary;
- simple totals where safe;
- no accounting claims.

Acceptance: dashboard gives useful operational orientation without becoming analytics software.

### `v0.7.x` - Simple Payments

- payment records linked to documents;
- paid, partial and pending calculation;
- small payment UI;
- tests for state calculation.

Acceptance: payments improve document tracking without turning FactX into an accounting product.

### `v0.8.x` - UX And Reliability

- empty states;
- better error messages;
- table usability;
- keyboard-friendly basic flows;
- more unit coverage;
- clean failure handling.

Acceptance: app feels intentional and stable enough for repeated demo use.

### `v0.9.x` - Portfolio Release Candidate

- clean clone verification;
- final screenshot pass;
- changelog cleanup;
- README narrative polish;
- repository audit for sensitive files;
- demo script.

Acceptance: no known blockers remain before `v1.0.0`.

### `v1.0.0` - Stable Portfolio Version

- stable local desktop app;
- clear project story;
- reproducible setup;
- screenshots and demo data;
- no sensitive files;
- no out-of-scope features.

Acceptance: FactX can be confidently shown in LinkedIn, GitHub and interviews as a complete v1 portfolio project.

## Post-v1: Optional External Sales And Billing Integration

After `v1.0.0`, FactX may evaluate an optional, decoupled integration that lets FijaStock submit versioned snapshots of completed external sales for later documentation or billing workflows. This is deliberately outside the committed v1 scope: it does not add an API, FijaStock dependency, shared database, ARCA integration or issued-invoice behavior to any v0.x or v1 milestone.

If approved later, the work should begin with a separate sale aggregate, a versioned import contract, database-enforced external idempotency and a transport-agnostic `SaleImportService` boundary. See [Future Integrations](FUTURE_INTEGRATIONS.md) for the retained context and constraints.

## First LinkedIn Milestone

`v0.3.0` remains the best first LinkedIn milestone.

Reason: by `v0.3.0`, FactX has real user-visible value: suppliers, documents, filters, states and demo data. Earlier versions are technically important but too invisible for a public portfolio post. Later versions risk delaying feedback and making the project look overplanned.

## First GitHub Release

`v0.3.0` should be the first formal GitHub Release.

Tags before `v0.3.0` are useful technical milestones. A GitHub Release should mean "this is worth downloading, reading and trying as a public checkpoint." That bar is first met when the app has a demonstrable business workflow.

## Legacy Reuse Rules

Allowed to inspect and rewrite ideas from `_factx_legacy_local/`:

- supplier, document and payment model ideas;
- simple DAO patterns;
- JavaFX layout ideas for suppliers and documents;
- CSS component ideas;
- local storage/path handling;
- PDF helper ideas later in attachments work.

Do not reintroduce:

- Gradle;
- old migrations wholesale;
- login, roles, users or auth;
- sync queue or sync worker;
- OCR, scanning, AI or dataset export;
- multi-sucursal;
- real comprobantes, images, PDFs or zip files;
- old package names or messy architecture.

Legacy code should be treated as reference material, not copied directly.

## Risks

- Timezone mismatch can break PostgreSQL demos if not normalized in `v0.0.4`.
- Docker orphan containers can confuse validation if not documented.
- Starting UI before repositories/services are stable can create hard-to-test code.
- Pulling too much legacy code can pollute the clean reset.
- Adding payments too early can make the scope feel like accounting software.
- Publishing before `v0.3.0` may look like infrastructure instead of a product.
- Overpolishing before core workflows may delay the useful portfolio milestone.
