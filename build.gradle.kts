import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"

    `maven-publish`
    signing
}

group = "dev.mlyngvo"
if (version == "unspecified") {
    version = "0.0.1-SNAPSHOT"
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "jwt-spring"

            pom {
                name.set("jwt-spring")
                description.set("A Spring Boot auto-configuration library that adds JWT-based authentication to any Spring Boot application.")
                url.set("https://github.com/mlyngvo/jwt-spring")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("mlyngvo")
                        name.set("Ly Nguyen")
                        email.set("contact@minhlynguyenvo.com")
                    }
                }

                scm {
                    url.set("https://github.com/mlyngvo/jwt-spring")
                    connection.set("scm:git:git://github.com/mlyngvo/jwt-spring.git")
                    developerConnection.set("scm:git:ssh://git@github.com/mlyngvo/jwt-spring.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    val signingKey = findProperty("signingInMemoryKey") as String?
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
    val signingPassword = findProperty("signingInMemoryKeyPassword") as String?
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
    isRequired = !signingKey.isNullOrBlank()
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    }
}

repositories {
    mavenCentral()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xjsr305=strict"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")   // remove -plain
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-security")
    api("io.jsonwebtoken:jjwt-api:0.12.5")
    api("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.flywaydb:flyway-mysql")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    testRuntimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}

kotlin {
    jvmToolchain(17)
}
