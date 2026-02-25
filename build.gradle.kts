import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    id("maven-publish")

    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    kotlin("plugin.jpa") version "2.2.0"

    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mlyngvo"
            artifactId = "jwt-spring"

            from(components["java"])
        }
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
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")   // remove
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
