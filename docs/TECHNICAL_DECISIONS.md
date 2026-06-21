# Technical Decisions

## Java 21

FactX uses Java 21 because it is a modern LTS version and fits the portfolio goal of showing current Java skills.

## Maven

FactX resets to Maven for a simple, widely recognized Java build structure.

## JavaFX

FactX is a desktop app. JavaFX keeps the UI local, inspectable and appropriate for a portfolio project focused on Java.

## PostgreSQL

PostgreSQL is used for realistic relational persistence during development and demos.

## Flyway

Flyway records schema evolution explicitly and keeps database changes reviewable.

## HikariCP

HikariCP provides a small, standard JDBC connection pool.

## Jdbi

Jdbi keeps persistence explicit without adding a heavy ORM layer.

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
