plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("maven-publish")

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com.mlyngvo"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
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
