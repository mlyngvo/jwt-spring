import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "4.0.10"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"

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

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")   // remove -plain
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("reflect"))

    api("org.springframework.boot:spring-boot-starter-webmvc")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-flyway")
    api("io.jsonwebtoken:jjwt-api:0.13.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.flywaydb:flyway-mysql")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    testRuntimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
