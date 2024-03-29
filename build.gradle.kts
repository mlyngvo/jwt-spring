import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("maven-publish")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mlyngvo/jwt-spring")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mlyngvo"
            artifactId = "jwt-spring"

            from(components["java"])
        }
    }
}

group = "com.mlyngvo"

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