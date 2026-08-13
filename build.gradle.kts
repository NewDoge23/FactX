import org.gradle.api.tasks.JavaExec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.4.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
    id("org.jetbrains.compose") version "1.11.1"
}

group = "ar.com.gaston"
version = "0.0.11"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.9.0")

    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:10.20.1")
    implementation("org.flywaydb:flyway-database-postgresql:10.20.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jdbi:jdbi3-core:3.45.4")
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    implementation("org.slf4j:slf4j-api:2.0.16")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.12")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test {
    useJUnitPlatform()
}

fun registerTechnicalCheck(taskName: String, mainClassName: String, descriptionText: String) {
    tasks.register<JavaExec>(taskName) {
        group = "verification"
        description = descriptionText
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set(mainClassName)
    }
}

registerTechnicalCheck("databaseCheck", "ar.com.gaston.factx.tools.DatabaseCheck", "Verifies the PostgreSQL connection and Flyway migrations.")
registerTechnicalCheck("repositoryCheck", "ar.com.gaston.factx.tools.RepositoryCheck", "Verifies repository round trips against PostgreSQL.")
registerTechnicalCheck("demoDataLoader", "ar.com.gaston.factx.tools.DemoDataLoader", "Loads the deterministic FactX demo dataset into PostgreSQL.")

compose.desktop {
    application {
        mainClass = "ar.com.gaston.factx.ui.FactXDesktopKt"
    }
}
